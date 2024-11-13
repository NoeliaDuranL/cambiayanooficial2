package com.example.cambiayanooficial2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewPublicaciones: RecyclerView
    private lateinit var publicacionAdapter: PublicacionAdapter
    private lateinit var addProductIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        val username = sharedPref.getString("username", null)
        val email = sharedPref.getString("email", null)
        val fullName = sharedPref.getString("fullName", null)

        Log.d("SharedPreferences", "isLoggedIn: $isLoggedIn, username: $username, email: $email, fullName: $fullName")


        // Inicializa el RecyclerView y el adaptador
        recyclerViewPublicaciones = findViewById(R.id.recyclerViewPublicaciones)
        recyclerViewPublicaciones.layoutManager = LinearLayoutManager(this)

        // Configurar BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Configurar el listener para detectar los clics en los ítems del menú
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_add_product -> {
                    // Cuando se hace clic en el ícono de "Añadir Producto"
                    val intent = Intent(this, AgregarProductoActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_home -> {
                    // Lógica para la opción de "Home"
                    true
                }
                R.id.nav_notifications -> {
                    // Lógica para la opción de "Notificaciones"
                    true
                }
                R.id.nav_chat -> {
                    // Lógica para la opción de "Chat"
                    true
                }
                else -> false
            }
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
