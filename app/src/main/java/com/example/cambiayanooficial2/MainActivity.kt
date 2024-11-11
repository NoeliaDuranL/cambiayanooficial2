package com.example.cambiayanooficial2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewPublicaciones: RecyclerView
    private lateinit var publicacionAdapter: PublicacionAdapter
    private lateinit var addProductIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa el RecyclerView y el adaptador
        recyclerViewPublicaciones = findViewById(R.id.recyclerViewPublicaciones)
        recyclerViewPublicaciones.layoutManager = LinearLayoutManager(this)

        // Encuentra el ícono de agregar producto
        addProductIcon = findViewById(R.id.addProductIcon)

        // Configura el OnClickListener para el ícono de agregar producto
        addProductIcon.setOnClickListener {
            val intent = Intent(this, AgregarProductoActivity::class.java)
            startActivity(intent)
        }

        // Inicializa el adaptador con una lista vacía
        publicacionAdapter = PublicacionAdapter(emptyList()) { publicacion ->
            iniciarChatConPublicador(publicacion)
        }
        recyclerViewPublicaciones.adapter = publicacionAdapter

        // Llamar a la función para cargar publicaciones desde la API
        cargarPublicacionesDesdeApi()
    }

    private fun cargarPublicacionesDesdeApi() {
        lifecycleScope.launch {
            try {
                // Llamada a la API para obtener publicaciones
                val publicaciones = RetrofitInstance.api.getPosts()
                Log.d("MainActivity", "Publicaciones recibidas: $publicaciones")

                // Actualiza el adaptador con las publicaciones recibidas
                publicacionAdapter = PublicacionAdapter(publicaciones) { publicacion ->
                    iniciarChatConPublicador(publicacion)
                }
                recyclerViewPublicaciones.adapter = publicacionAdapter

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MainActivity", "Error al cargar publicaciones: ${e.message}")
            }
        }
    }

    private fun iniciarChatConPublicador(publicacion: Publicacion) {
        println("Iniciar chat con: ${publicacion.usuario.usuario}")
    }
}
