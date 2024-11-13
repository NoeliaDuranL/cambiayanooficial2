package com.example.cambiayanooficial2

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
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var etProductName: EditText
    private lateinit var etProductDescription: EditText
    private lateinit var btnPublishProduct: Button
    private val REQUEST_CODE_IMAGE = 1001
    private var selectedImageUri: Uri? = null
    private var productoId: Int? = null // Variable para almacenar el id_producto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_producto)

        // Inicializar vistas
        etProductName = findViewById(R.id.etProductName)
        etProductDescription = findViewById(R.id.etProductDescription)
        btnPublishProduct = findViewById(R.id.btnPublishProduct)
        val imageContainer = findViewById<LinearLayout>(R.id.imageContainer)

        // Configurar el contenedor de imagen para seleccionar una imagen
        imageContainer.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_IMAGE)
        }

        // Configurar el botón para publicar
        btnPublishProduct.setOnClickListener {
            crearProducto() // Primero crea el producto
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            Toast.makeText(this, "Imagen seleccionada, sube el producto para asociarla", Toast.LENGTH_SHORT).show()
        }
    }

    private fun crearProducto() {
        val nombre = etProductName.text.toString()
        val descripcion = etProductDescription.text.toString()

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val productoRequest = PostProductoRequest(
                    nombreProducto = nombre,
                    descripcionProducto = descripcion,
                    imagenProducto = null, // Deja null, la imagen se sube después
                    descripcionPost = descripcion
                )

                val postResponse = RetrofitInstance.api.crearProductoYPost(productoRequest)
                productoId = postResponse.id // Guarda el id_producto devuelto
                Toast.makeText(this@AgregarProductoActivity, "Producto creado con éxito", Toast.LENGTH_SHORT).show()

                // Si hay una imagen seleccionada, súbela
                selectedImageUri?.let { uploadImageToServer(it) }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@AgregarProductoActivity, "Error al crear el producto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageToServer(imageUri: Uri) {
        val file = File(getRealPathFromURI(imageUri))
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        // Crea un request body para el id_producto
        val productoIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), productoId.toString())

        RetrofitInstance.api.uploadImage(body, productoIdBody).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AgregarProductoActivity, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@AgregarProductoActivity, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Toast.makeText(this@AgregarProductoActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }

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
