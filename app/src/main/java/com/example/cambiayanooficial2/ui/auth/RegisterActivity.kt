package com.example.cambiayanooficial2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cambiayanooficial2.R
import com.example.cambiayanooficial2.models.request.RegisterRequest
import com.example.cambiayanooficial2.network.ApiClient
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var darkOverlay: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicialización de las vistas
        usernameEditText = findViewById(R.id.username)
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        registerButton = findViewById(R.id.register_button)
        loadingProgressBar = findViewById(R.id.loading)
        darkOverlay = findViewById(R.id.dark_overlay)

        // Redirigir a LoginActivity al hacer clic en el texto de login
        findViewById<TextView>(R.id.login_text).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Validación en tiempo real con TextWatcher
        usernameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateUsername()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateEmail()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePassword()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Registro del usuario
        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (validateUsername() && validateEmail() && validatePassword()) {
                // Ejecutar la llamada en segundo plano utilizando coroutines
                registerUser(username, email, password)
            } else {
                Toast.makeText(this, "Por favor completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateUsername(): Boolean {
        val username = usernameEditText.text.toString().trim()
        return if (username.isEmpty()) {
            usernameEditText.error = "El nombre de usuario es requerido"
            false
        } else if (username.length < 3) {
            usernameEditText.error = "El nombre de usuario debe tener al menos 3 caracteres"
            false
        } else {
            usernameEditText.error = null // Limpiar error si es válido
            true
        }
    }

    private fun validateEmail(): Boolean {
        val email = emailEditText.text.toString().trim()
        return if (email.isEmpty()) {
            emailEditText.error = "El correo es requerido"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Correo inválido"
            false
        } else {
            emailEditText.error = null // Limpiar error si es válido
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = passwordEditText.text.toString().trim()
        return if (password.isEmpty()) {
            passwordEditText.error = "La contraseña es requerida"
            false
        } else if (password.length < 8) {
            passwordEditText.error = "La contraseña debe tener al menos 8 caracteres"
            false
        } else {
            passwordEditText.error = null // Limpiar error si es válido
            true
        }
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            darkOverlay.visibility = View.VISIBLE
            loadingProgressBar.visibility = View.VISIBLE
        } else {
            darkOverlay.visibility = View.GONE
            loadingProgressBar.visibility = View.GONE
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
                // Mostrar loading
                setLoading(true)

                val apiService = ApiClient.apiService
                val response = apiService.register(registerRequest)

                // Verificar si la respuesta es exitosa
                if (response.success) {
                    setLoading(false)
                    Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    setLoading(false)
                    Toast.makeText(this@RegisterActivity, response.message ?: "Error desconocido", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                setLoading(false)
                Toast.makeText(this@RegisterActivity, "Error en la conexión con el servidor", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                setLoading(false)
                Toast.makeText(this@RegisterActivity, "Fallo en la conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
