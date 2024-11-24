package com.example.cambiayanooficial2.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cambiayanooficial2.R
import com.example.cambiayanooficial2.models.Publicacion
import com.example.cambiayanooficial2.models.request.NotificationRequest
import com.example.cambiayanooficial2.models.response.PostResponse
import com.example.cambiayanooficial2.network.ApiClient
import com.example.cambiayanooficial2.ui.adapter.PublicacionAdapter
import com.example.cambiayanooficial2.ui.product.ProductActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewPublicaciones: RecyclerView
    private lateinit var publicacionAdapter: PublicacionAdapter
    private lateinit var addProductIcon: ImageView
    // Declaramos una variable global para el token FCM
    private var fcmToken: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fcmToken = task.result
                // Aquí puedes ver el token en el log
                Log.d("FCM Token", "Token FCM obtenido: $fcmToken")

                // Si deseas, puedes enviar el token al servidor para almacenarlo
//                sendTokenToServer(token)
            } else {
                Log.w("FCM Token", "Obtener token falló", task.exception)
            }
        }
        // Crear canal de notificación para Android 8.0 y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_channel"
            val channelName = "Notificaciones"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = "Canal para notificaciones"

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        recyclerViewPublicaciones = findViewById(R.id.recyclerViewPublicaciones)
        recyclerViewPublicaciones.layoutManager = LinearLayoutManager(this)

        // Configurar BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_add_product -> {
                    val intent = Intent(this, ProductActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_home -> true
                R.id.nav_notifications -> {
                    val intent = Intent(this, NotificationActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.nav_home

        publicacionAdapter = PublicacionAdapter(emptyList()) { publicacion ->
            manejarMeInteresa(publicacion) // Llamar a la función cuando se hace clic
        }
        recyclerViewPublicaciones.adapter = publicacionAdapter

        cargarPublicacionesDesdeApi()
    }

    private fun cargarPublicacionesDesdeApi() {
        lifecycleScope.launch {
            try {
                val response: PostResponse = ApiClient.apiService.getPosts()
                if (response.success) {
                    publicacionAdapter = PublicacionAdapter(response.data) { publicacion ->
                        manejarMeInteresa(publicacion) // Esto se llama cuando se hace clic en el botón "Me Interesa"
                    }

                    recyclerViewPublicaciones.adapter = publicacionAdapter

                } else {
                    Log.e("MainActivity", "Error: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error al cargar publicaciones: ${e.message}")
            }
        }
    }


// En alguna actividad o servicio (por ejemplo, MainActivity)



    private fun manejarMeInteresa(publicacion: Publicacion) {
        val userId = getUserId()
        if (userId != null) {

            val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val username = sharedPref.getString("username", "Usuario no definido")
            val mensaje = "A ${username} le interesa tu publicación: ${publicacion.producto.nombre}"
            Log.d("MainActivity", "Usuario ID: $userId, Publicación ID: ${publicacion.id_post}")
            Log.d("MainActivity", "Mensaje: $mensaje")

            // 1. Mostrar la notificación local al usuario
            mostrarNotificacionLocal(this, mensaje)

            // 2. Crear el objeto de notificación para enviar al backend
            val notificacionRequest = NotificationRequest(
                id_usuario = userId,
                id_remitente = publicacion.id_usuario,
                id_post = publicacion.id_post,
                token = fcmToken,
                mensaje = mensaje
            )
            // 3. Enviar la notificación al backend
            Log.d("MainActivity", "Verificando el request: ${notificacionRequest}")
            lifecycleScope.launch {
                try {


                    val response = ApiClient.apiService.enviarNotificacionMeInteresa(notificacionRequest)
                    if (response.success) {
                        Toast.makeText(this@MainActivity, "Te interesa este producto", Toast.LENGTH_SHORT).show()
                        Log.d("MainActivity", "Notificación enviada correctamente al backend")
                    } else {
                        // Si la respuesta no es exitosa, revisa el cuerpo de la respuesta para más detalles
                        Log.e("MainActivity", "Error al enviar la notificación al backend: ${response.message}")
                        Log.d("MainActivity", "Cuerpo de la respuesta del backend: ${response}")
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error al enviar la notificación: ${e.message}")
                }
            }
        }
    }

    private fun getUserId(): Int? {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val id = sharedPref.getInt("id", -1)
        return if (id != -1) id else null
    }

    fun mostrarNotificacionLocal(context: Context, mensaje: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificación para Android 8.0 y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "me_interesa_channel"
            val channelName = "Me Interesa Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = "Notificaciones para el botón Me Interesa"
            notificationManager.createNotificationChannel(channel)
        }

        // Crear la notificación
        val notification = NotificationCompat.Builder(context, "me_interesa_channel")
            .setContentTitle("Notificación de Interés")
            .setContentText(mensaje)
            .setSmallIcon(R.drawable.ic_notifications) // Reemplaza con tu ícono
            .setAutoCancel(true)
            .build()

        // Mostrar la notificación
        notificationManager.notify(1, notification)
    }
}
