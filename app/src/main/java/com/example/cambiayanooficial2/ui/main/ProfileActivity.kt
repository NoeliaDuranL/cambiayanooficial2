package com.example.cambiayanooficial2.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cambiayanooficial2.R
import com.example.cambiayanooficial2.ui.auth.LoginActivity
//import com.example.cambiayanooficial2.ui.product.AgregarProductoActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Obtener el BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Obtener los TextView del layout
        val userNameTextView = findViewById<TextView>(R.id.tv_user_name)
        val userEmailTextView = findViewById<TextView>(R.id.tv_user_email)
        val logoutButton = findViewById<Button>(R.id.btn_logout)

        // Obtener SharedPreferences
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        // Si no está logueado, redirigir al login
        if (!isLoggedIn) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Finaliza la actividad actual
            return
        }

        // Recuperar datos del usuario
        val username = sharedPref.getString("username", "Usuario no definido")
        val email = sharedPref.getString("email", "Correo no definido")

        // Mostrar los datos en los TextView
        userNameTextView.text = username
        userEmailTextView.text = email

        // Configurar el botón de cerrar sesión
        logoutButton.setOnClickListener {
            cerrarSesion() // Llamar al método para cerrar sesión
        }

        // Seleccionar el item de navegación de perfil
        bottomNavigationView.selectedItemId = R.id.nav_profile

        // Configurar el listener de navegación
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_add_product -> {
//                    val intent = Intent(this, AgregarProductoActivity::class.java)
//                    startActivity(intent)
//                    finish() // Finaliza la actividad actual para evitar apilamiento
                    true
                }
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_notifications -> {
                    // Implementar la lógica para ir a la actividad de notificaciones
                    true
                }
                R.id.nav_chat -> {
                    // Implementar la lógica para ir a la actividad de chat
                    true
                }
                R.id.nav_profile -> {
                    // Ya estás en ProfileActivity, no hagas nada
                    true
                }
                else -> false
            }
        }
    }

    // Método para cerrar sesión
    private fun cerrarSesion() {
        // Obtener SharedPreferences
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Limpiar los datos de sesión
        editor.clear()
        editor.apply()

        // Agregar un log para asegurarse de que se está limpiando la sesión
        Log.d("ProfileActivity", "Sesión cerrada correctamente")

        // Redirigir al usuario al inicio de sesión
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Finaliza la actividad actual
    }

    // Método para gestionar la acción de retroceso
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish() // Finaliza la actividad actual para evitar duplicados
        super.onBackPressed() // Llama al comportamiento base del sistema
    }
}
