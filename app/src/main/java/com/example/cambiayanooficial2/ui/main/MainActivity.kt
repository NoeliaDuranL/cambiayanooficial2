package com.example.cambiayanooficial2.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cambiayanooficial2.R
import com.example.cambiayanooficial2.models.Publicacion
import com.example.cambiayanooficial2.models.response.PostResponse
import com.example.cambiayanooficial2.network.ApiClient
import com.example.cambiayanooficial2.ui.adapter.PublicacionAdapter
import com.example.cambiayanooficial2.ui.product.ProductActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewPublicaciones: RecyclerView
    private lateinit var publicacionAdapter: PublicacionAdapter
    private lateinit var addProductIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar RecyclerView
        recyclerViewPublicaciones = findViewById(R.id.recyclerViewPublicaciones)
        recyclerViewPublicaciones.layoutManager = LinearLayoutManager(this)

        // Configurar BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNavigationView.selectedItemId = R.id.nav_home

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_add_product -> {
                    val intent = Intent(this, ProductActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_home -> true
                R.id.nav_notifications -> true
                R.id.nav_chat -> true
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }

        // Inicializar el adaptador con una lista vacía
        publicacionAdapter = PublicacionAdapter(emptyList()) { publicacion ->
            iniciarChatConPublicador(publicacion)
        }
        recyclerViewPublicaciones.adapter = publicacionAdapter

        // Llamar a la función para cargar publicaciones
        cargarPublicacionesDesdeApi()
    }

    private fun cargarPublicacionesDesdeApi() {
        lifecycleScope.launch {
            try {
                // Obtener publicaciones desde la API
                val response: PostResponse = ApiClient.apiService.getPosts()

                if (response.success) {
                    Log.d("MainActivity", "Publicaciones cargadas: ${response.data}")

                    // Actualizar el adaptador con los datos recibidos
                    publicacionAdapter = PublicacionAdapter(response.data) { publicacion ->
                        iniciarChatConPublicador(publicacion)
                    }
                    recyclerViewPublicaciones.adapter = publicacionAdapter
                } else {
                    Log.e("MainActivity", "Error: ${response.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MainActivity", "Error al cargar publicaciones: ${e.message}")
            }
        }
    }

    private fun iniciarChatConPublicador(publicacion: Publicacion) {
        Log.d("MainActivity", "Iniciar chat con: ${publicacion.usuario.usuario}")
    }
}
