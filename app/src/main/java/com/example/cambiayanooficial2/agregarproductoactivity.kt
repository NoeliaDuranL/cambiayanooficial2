package com.example.cambiayanooficial2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var etProductName: EditText
    private lateinit var etProductDescription: EditText
    private lateinit var btnPublishProduct: Button
    private lateinit var selectedImage: ImageView
    private var imageUri: Uri? = null // Variable para almacenar la URI de la imagen seleccionada

    companion object {
        private const val REQUEST_IMAGE_PICK = 1000 // Código de solicitud para el selector de imágenes
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_producto)

        // Inicializar vistas
        etProductName = findViewById(R.id.etProductName)
        etProductDescription = findViewById(R.id.etProductDescription)
        selectedImage = findViewById(R.id.selectedImage)
        btnPublishProduct = findViewById(R.id.btnPublishProduct)

        // Configurar el contenedor de imagen para seleccionar una imagen
        findViewById<LinearLayout>(R.id.imageContainer).setOnClickListener {
            selectImageFromGallery()
        }

        // Configurar el botón para publicar
        btnPublishProduct.setOnClickListener {
            publicarProducto()
        }
    }

    // Función para abrir la galería y seleccionar una imagen
    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    // Manejar el resultado de la selección de imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            selectedImage.setImageURI(imageUri) // Mostrar la imagen seleccionada en el ImageView
        }
    }

    private fun publicarProducto() {
        val nombre = etProductName.text.toString()
        val descripcion = etProductDescription.text.toString()

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // Crea el objeto con los datos necesarios para el request
                val productoRequest = PostProductoRequest(
                    nombreProducto = nombre,
                    descripcionProducto = descripcion,
                    imagenProducto = imageUri.toString(), // Usamos la URI de la imagen
                    descripcionPost = descripcion  // Puedes cambiar esto si quieres un contenido diferente en la publicación
                )

                // Llama a la API para crear el producto y el post
                val postResponse = RetrofitInstance.api.crearProductoYPost(productoRequest)
                Toast.makeText(this@AgregarProductoActivity, "Producto publicado con éxito", Toast.LENGTH_SHORT).show()
                finish() // Cierra la actividad después de publicar
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@AgregarProductoActivity, "Error al publicar el producto", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
