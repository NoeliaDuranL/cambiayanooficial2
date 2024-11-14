package com.example.cambiayanooficial2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cambiayanooficial2.R
import com.example.cambiayanooficial2.network.ApiClient
import com.example.cambiayanooficial2.models.request.RegisterRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        usernameEditText = findViewById(R.id.username)
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        registerButton = findViewById(R.id.register_button)
        // Redirigir a LoginActivity al hacer clic en el texto de login
        findViewById<TextView>(R.id.login_text).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                // Ejecutar la llamada en segundo plano utilizando coroutines
                registerUser(username, email, password)
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(username: String, email: String, password: String) {
        val registerRequest = RegisterRequest(
            id_persona = 1,  // Cambia a tu ID real
            usuario = username,
            correo = email,
            contrasena = password
        )

        // Usar CoroutineScope para manejar la llamada en segundo plano
        lifecycleScope.launch {
            try {
                val apiService = ApiClient.apiService
                val response = apiService.register(registerRequest)

                // Verificar si la respuesta es exitosa
                if (response.success) {
                    Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, response.message ?: "Error desconocido", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                // Manejo de errores HTTP (como error de servidor, 404, 500, etc.)
                Toast.makeText(this@RegisterActivity, "Error en la conexión con el servidor", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Manejo de errores generales (como fallo en la conexión a Internet)
                Toast.makeText(this@RegisterActivity, "Fallo en la conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }
}