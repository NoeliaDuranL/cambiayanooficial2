package com.example.cambiayanooficial2.ui.product

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cambiayanooficial2.R
import com.example.cambiayanooficial2.models.request.Product
import com.example.cambiayanooficial2.models.response.ImageUploadResponse
import com.example.cambiayanooficial2.models.response.responseProducto
import com.example.cambiayanooficial2.network.ApiClient
import com.example.cambiayanooficial2.ui.main.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var etProductName: EditText
    private lateinit var etProductDescription: EditText
    private lateinit var btnPublishProduct: Button
    private var selectedImageUri: Uri? = null
    private var productoId: Int? = null // ID del producto que se asignará después de crear el producto

    private val REQUEST_CODE_IMAGE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_producto)

        // Inicializar vistas
        etProductName = findViewById(R.id.etProductName)
        etProductDescription = findViewById(R.id.etProductDescription)
        btnPublishProduct = findViewById(R.id.btnPublishProduct)

        // Configurar el botón para seleccionar una imagen
        findViewById<LinearLayout>(R.id.imageContainer).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_IMAGE)
        }

        // Configurar el botón para publicar el producto
        btnPublishProduct.setOnClickListener {
            selectedImageUri?.let { imageUri ->
                uploadImage(imageUri)  // Subir imagen al servidor
            } ?: run {
                // Si no hay imagen seleccionada, mostrar un mensaje
                Toast.makeText(this, "Por favor selecciona una imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show()
        }
    }

    // Crear producto sin la imagen inicialmente
    private fun crearProductoConImagen(imagePath: String) {
        val nombre = etProductName.text.toString()
        val descripcion = etProductDescription.text.toString()

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // Crear objeto Producto con la imagen que ya se subió
                val product = Product(nombre = nombre, descripcion = descripcion, imagen = imagePath)

                // Llamada al API para crear el producto con la imagen
                val response = ApiClient.apiService.createProduct(product)

                if (response.isSuccessful) {
                    productoId = response.body()?.productId
                    Toast.makeText(this@AgregarProductoActivity, "Producto creado con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@AgregarProductoActivity, "Error al crear el producto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@AgregarProductoActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Subir la imagen al servidor
    private fun uploadImage(imageUri: Uri) {
        val file = File(getRealPathFromURI(imageUri))
        val requestFile = file.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        // Aquí puedes obtener el productoId, si ya lo tienes en el contexto, o inicializarlo aquí
        val productIdBody = productoId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        ApiClient.apiService.uploadImage(body, productIdBody).enqueue(object : Callback<ImageUploadResponse> {
            override fun onResponse(call: Call<ImageUploadResponse>, response: Response<ImageUploadResponse>) {
                if (response.isSuccessful) {
                    // Obtener el nombre de la imagen del servidor (imagePath)
                    val imagePath = response.body()?.imagePath
                    if (imagePath != null) {
                        // Ahora, crea el producto con la imagen subida
                        crearProductoConImagen(imagePath)
                    } else {
                        Toast.makeText(this@AgregarProductoActivity, "Error al obtener el nombre de la imagen", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AgregarProductoActivity, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {
                Toast.makeText(this@AgregarProductoActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Actualizar el producto con el nombre de la imagen
    private fun actualizarProductoConImagen(imagePath: String?) {
        if (productoId != null && !imagePath.isNullOrEmpty()) {
            lifecycleScope.launch {
                try {
                    val updatedProduct = Product(
                        nombre = etProductName.text.toString(),
                        descripcion = etProductDescription.text.toString(),
                        imagen = imagePath // El nombre de la imagen guardado en el servidor
                    )
                    // Aquí debes enviar una solicitud PUT o PATCH para actualizar el producto
                    // ApiClient.apiService.updateProduct(productoId!!, updatedProduct)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Obtener el path real de la URI de la imagen
    private fun getRealPathFromURI(uri: Uri): String {
        var path = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            val idx = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            path = it.getString(idx)
        }
        return path
    }
}


