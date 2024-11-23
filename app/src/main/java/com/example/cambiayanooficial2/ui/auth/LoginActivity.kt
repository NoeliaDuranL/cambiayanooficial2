package com.example.cambiayanooficial2.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cambiayanooficial2.R
import com.example.cambiayanooficial2.databinding.ActivityLoginBinding
import com.example.cambiayanooficial2.models.request.LoginRequest
import com.example.cambiayanooficial2.models.response.ApiResponse
import com.example.cambiayanooficial2.network.ApiClient
import com.example.cambiayanooficial2.ui.main.MainActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            performLogin()
            preventMultipleClicks()
        }

        binding.registerText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this) // Si no hay vista con foco, crea una vista temporal
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }




    private fun performLogin() {
        // Llamar a hideKeyboard para ocultar el teclado cuando se presiona el botón
        hideKeyboard()
        val usernameLayout = findViewById<TextInputLayout>(R.id.username_layout)
        val passwordLayout = findViewById<TextInputLayout>(R.id.password_layout)
        val username = binding.username.text.toString().trim()
        val password = binding.password.text.toString().trim()

        // Validación de campos vacíos y longitud mínima
        if (username.isBlank() || password.isBlank()) {
            showError("Por favor, complete todos los campos")
            return
        }
//        if (username.length < 3) {
//            showError("El nombre de usuario debe tener al menos 3 caracteres")
//            retur
//        }
//        if (password.length < 6) {
//            showError("La contraseña debe tener al menos 6 caracteres")
//            return
//        }

        val loginRequest = LoginRequest(usuario = username, contrasena = password)
        Log.d("LoginRequest", "Usuario: ${loginRequest.usuario}")

        lifecycleScope.launch {
            setLoading(true) // Mostrar indicador de carga
            try {
                val response: Response<ApiResponse> = ApiClient.apiService.login(loginRequest)
                setLoading(false) // Ocultar indicador de carga
                Log.d("LoginResponse", "Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == true) {
                        saveUserData(responseBody)
                        navigateToMainActivity(responseBody)
                    } else {
                        showError(responseBody?.message ?: "Datos incorrectos")
                    }
                } else {
                    val errorMessage = parseErrorResponse(response.errorBody()?.string())
                    showError(errorMessage)
                }
            } catch (e: Exception) {
                setLoading(false)
                Log.e("LoginError", "Error: ${e.localizedMessage}")
                showError("Error: ${e.localizedMessage}")
            }
        }
    }

    private fun saveUserData(responseBody: ApiResponse) {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isLoggedIn", true)
            responseBody.user?.let {
                putInt("id", it.id_usuario) // Guarda el ID como entero
                putString("username", it.usuario)
                putString("email", it.correo)
                putString("fullName", "${it.nombre} ${it.apellido}")
            }
            apply()
        }

    }

    private fun navigateToMainActivity(responseBody: ApiResponse) {
        Toast.makeText(
            this,
            "Bienvenido, ${responseBody.user?.nombre} ${responseBody.user?.apellido}",
            Toast.LENGTH_SHORT
        ).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun parseErrorResponse(errorBody: String?): String {
        if (errorBody.isNullOrEmpty()) return "Datos incorrectos"
        return try {
            val apiResponse = Gson().fromJson(errorBody, ApiResponse::class.java)
            apiResponse.message ?: "Datos incorrectos"
        } catch (e: Exception) {
            "Error inesperado"
        }
    }

    private fun showError(message: String) {
        binding.errorMessage.text = message
        binding.errorMessage.visibility = View.VISIBLE

        // Resaltar los campos vacíos
        if (binding.username.text.toString().isBlank()) {
            binding.usernameLayout.error = "Campo requerido"
        } else {
            binding.usernameLayout.error = null
        }

        if (binding.password.text.toString().isBlank()) {
            binding.passwordLayout.error = "Campo requerido"
        } else {
            binding.passwordLayout.error = null
        }
    }

    private fun setLoading(isLoading: Boolean) {
        val darkOverlay = findViewById<View>(R.id.dark_overlay)
        val progressBar = findViewById<ProgressBar>(R.id.loading)

        if (isLoading) {
            darkOverlay.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE

        } else {
            darkOverlay.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }


    private fun preventMultipleClicks() {
        binding.loginButton.isEnabled = false
        binding.loginButton.postDelayed({
            binding.loginButton.isEnabled = true
        }, 2000) // Deshabilita por 2 segundos
    }
}
