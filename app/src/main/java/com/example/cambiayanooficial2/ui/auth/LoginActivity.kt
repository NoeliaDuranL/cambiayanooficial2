package com.example.cambiayanooficial2.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cambiayanooficial2.ui.main.MainActivity
import com.example.cambiayanooficial2.R
import com.example.cambiayanooficial2.models.response.ApiResponse
import com.example.cambiayanooficial2.network.ApiClient
import com.example.cambiayanooficial2.models.request.LoginRequest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login_button)

        loginButton.setOnClickListener { performLogin() }

        findViewById<TextView>(R.id.register_text).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }


    private fun performLogin() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (username.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val loginRequest = LoginRequest(usuario = username, contrasena = password)
        Log.d("LoginRequest", "Usuario: ${loginRequest.usuario}, Contraseña: ${loginRequest.contrasena}")

        // Lanzar la corrutina para hacer la llamada de login
        lifecycleScope.launch {
            try {
                val response: ApiResponse = ApiClient.apiService.login(loginRequest)
                Log.d("LoginResponse", "Response: $response")

                if (response.success) {
                    // Guardar los datos del usuario en SharedPreferences
                    val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putBoolean("isLoggedIn", true)
                        // Verificar que los valores no sean nulos antes de guardarlos
                        response.user?.let {
                            putString("username", it.usuario)
                            putString("email", it.correo)
                            putString("fullName", "${it.nombre} ${it.apellido}")
                        }
                        apply() // o commit()
                    }


                    // Mostrar bienvenida
                    Toast.makeText(this@LoginActivity, "Bienvenido, ${response.user?.nombre} ${response.user?.apellido}", Toast.LENGTH_SHORT).show()

                    // Navegar a la actividad principal
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                // Manejo de error
                Toast.makeText(this@LoginActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }

    }
}