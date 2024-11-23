package com.example.cambiayanooficial2.ui.product

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cambiayanooficial2.R
import com.example.cambiayanooficial2.models.request.PostProductRequest
import com.example.cambiayanooficial2.network.ApiClient
import com.example.cambiayanooficial2.ui.main.MainActivity
import com.example.cambiayanooficial2.ui.main.ProfileActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class ProductActivity : AppCompatActivity() {

    private lateinit var etProductName: EditText
    private lateinit var ivProductImage: ImageView
    private lateinit var etDescription: EditText
    private lateinit var btnSelectImage: Button
    private lateinit var btnPublishProduct: Button
    private lateinit var progressBar: ProgressBar

    private var imageUri: Uri? = null
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_product)

        try {
            storageReference = FirebaseStorage.getInstance().reference
            Log.d("Firebase", "Firebase Storage está inicializado correctamente.")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("FirebaseError", "Error al inicializar Firebase Storage: ${e.message}")
        }

        // Configurar BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)


        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_add_product -> {
                    // No hacer nada aquí porque ya estamos en ProductActivity
                    true
                }
                R.id.nav_notifications -> {
                    // Aquí podrías abrir la actividad de notificaciones
                    true
                }
                R.id.nav_chat -> {
                    // Aquí podrías abrir la actividad de chat
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.nav_add_product // Asegúrate de que `nav_add_product` sea el seleccionado inicialmente

        etProductName = findViewById(R.id.etProductName)
        ivProductImage = findViewById(R.id.ivProductImage)
        etDescription = findViewById(R.id.etDescription)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnPublishProduct = findViewById(R.id.btnPublishProduct)
        progressBar = findViewById(R.id.progressBar)

        btnSelectImage.setOnClickListener {
            // Abrir selector de imagen (Cámara o Galería)
            ImagePicker.with(this)
                .crop()                    // Recortar imagen si es necesario
                .compress(1024)             // Comprimir la imagen (se mantiene por debajo de 1 MB)
                .maxResultSize(1080, 1080)  // Redimensionar la imagen a un máximo de 1080x1080
                .start()
        }

        btnPublishProduct.setOnClickListener {
            if (imageUri != null && etProductName.text.isNotEmpty() && etDescription.text.isNotEmpty()) {
                // Subir imagen a Firebase y luego enviar datos a la API
                progressBar.visibility = View.VISIBLE
                uploadImageToFirebase()
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para manejar la imagen seleccionada
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            ivProductImage.setImageURI(imageUri)  // Mostrar la imagen seleccionada en el ImageView
        }
    }

    // Subir la imagen seleccionada a Firebase Storage
    private fun uploadImageToFirebase() {
        val imageName = "products/${UUID.randomUUID()}.jpg"
        val fileReference = storageReference.child(imageName)

        fileReference.putFile(imageUri!!)
            .addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    val userId = getUserId() ?: -1 // Obtener el ID del usuario desde SharedPreferences
                    publishProduct(userId, imageUrl)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al subir la imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Enviar los datos del producto al backend Laravel
    private fun publishProduct(userId: Int, imageUrl: String) {
        val productName = etProductName.text.toString()
        val description = etDescription.text.toString()

        // Usar ApiClient para crear la instancia de ApiService
        val apiService = ApiClient.apiService

        val productRequest = PostProductRequest(
            name = productName,
            image_url = imageUrl,
            description = description,
            user_id = userId // Agregar el ID de usuario al objeto de solicitud
        )

        // Usar Retrofit para hacer la llamada POST
        apiService.postProduct(productRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductActivity, "Producto publicado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ProductActivity, "Error al publicar el producto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ProductActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        true
    }

    // Función para obtener el ID del usuario desde SharedPreferences
    private fun getUserId(): Int? {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getInt("id", -1).takeIf { it != -1 }
    }

    companion object {
        private const val REQUEST_CODE_CAMERA = 101
        private const val REQUEST_CODE_GALLERY = 102
    }
}
