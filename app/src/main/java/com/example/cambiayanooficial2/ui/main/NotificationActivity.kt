package com.example.cambiayanooficial2.ui.main

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cambiayanooficial2.R
import com.example.cambiayanooficial2.network.ApiClient
import com.example.cambiayanooficial2.ui.adapter.NotificationAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationActivity : AppCompatActivity() {

    private lateinit var notificationsRecyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // Inicializamos el RecyclerView
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView)

        // Configuramos el RecyclerView con LayoutManager y Adapter
        notificationsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Llamamos a la función para obtener las notificaciones
        val userId = getUserId()
        // Verificar que el userId no sea null antes de proceder
        if (userId != null) {
            // Si el userId es válido, pasarlo a la función fetchNotifications
            fetchNotifications(userId)
        }
    }

    private fun getUserId(): Int? {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // Obtener el ID guardado en SharedPreferences
        val id = sharedPref.getInt("id", -1)

        // Retornar el ID si es válido, o null si no está guardado
        return if (id != -1) id else null
    }

    private fun fetchNotifications(userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Pasar el id_usuario como parámetro
                val response = ApiClient.apiService.getNotifications(userId)

                if (response.success) {
                    if (response.data.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            notificationAdapter = NotificationAdapter(response.data)
                            notificationsRecyclerView.adapter = notificationAdapter
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@NotificationActivity, "No tienes notificaciones", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@NotificationActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@NotificationActivity, "Error al obtener las notificaciones", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
