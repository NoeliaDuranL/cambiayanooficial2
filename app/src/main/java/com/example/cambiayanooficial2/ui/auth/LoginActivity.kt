package com.example.cambiayanooficial2.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cambiayanooficial2.R
import com.example.cambiayanooficial2.databinding.ActivityLoginBinding
import com.example.cambiayanooficial2.models.DatosUsuario
import com.example.cambiayanooficial2.models.request.LoginRequest
import com.example.cambiayanooficial2.models.response.ApiResponse
import com.example.cambiayanooficial2.network.ApiClient
import com.example.cambiayanooficial2.ui.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configuración de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Asegúrate de tener el Client ID en `strings.xml`
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Botón de Google Sign-In
        binding.googleLoginButton.setOnClickListener {
            // Cerrar sesión de Google si ya está iniciada
            GoogleSignIn.getClient(this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut()

            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        binding.loginButton.setOnClickListener {
            performLogin()
            preventMultipleClicks()
        }

        binding.registerText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    // Actividad para manejar el resultado de la solicitud de inicio de sesión con Google
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task?.getResult(ApiException::class.java)

                if (account != null) {
                    firebaseAuthWithGoogle(account.idToken!!)
                }
            } else {
                Toast.makeText(this, "Inicio de sesión con Google fallido", Toast.LENGTH_SHORT).show()
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Obtener el correo del usuario de Google
                        val userEmail = user.email

                        // Realizar una solicitud a tu API usando el correo
                        userEmail?.let {
                            fetchUserIdFromApi(it) // Solicitar el ID desde la API
                        }
//                        val userData = DatosUsuario(
//                            username = user.displayName,  // El nombre de usuario (Google)
//                            email = user.email,           // El email (Google)
//                            fullName = user.displayName,  // El nombre completo (Google)
//                            id = null                     // No tenemos un ID de la API en este caso
//                        )
//                        saveUserData(userData)
//                        navigateToMainActivity()
                    }
                } else {
                    Log.w("LoginActivity", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun fetchUserIdFromApi(email: String) {
        lifecycleScope.launch {
            setLoading(true) // Mostrar indicador de carga
            try {
                // Realizar la solicitud a tu API para obtener el ID de usuario
                val response = ApiClient.apiService.getUserIdByEmail(email)

                setLoading(false) // Ocultar indicador de carga

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == true) {
                        val userData = DatosUsuario(
                            username = responseBody.usuario,
                            email = responseBody.correo,
                            fullName = "${responseBody.nombre} ${responseBody.apellido}",
                            id = responseBody.id_usuario  // Usar el ID obtenido desde la API
                        )
                        saveUserData(userData) // Guardar los datos del usuario
                        navigateToMainActivity() // Navegar al MainActivity
                    } else {
                        showError(responseBody?.message ?: "Error al obtener los datos del usuario")
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



    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
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

                        val userData = DatosUsuario(
                            username = responseBody.user?.usuario,
                            email = responseBody.user?.correo,
                            fullName = "${responseBody.user?.nombre} ${responseBody.user?.apellido}",
                            id = responseBody.user?.id_usuario  // Usar el ID de la API
                        )
//                        saveUserData(responseBody)
                        saveUserData(userData)
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

//    private fun saveUserData(responseBody: ApiResponse) {
//        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//        with(sharedPref.edit()) {
//            putBoolean("isLoggedIn", true)
//            responseBody.user?.let {
//                putInt("id", it.id_usuario) // Guarda el ID como entero
//                putString("username", it.usuario)
//                putString("email", it.correo)
//                putString("fullName", "${it.nombre} ${it.apellido}")
//            }
//            apply()
//        }
//
//    }

    private fun saveUserData(userData: DatosUsuario) {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isLoggedIn", true)

            // Guardar la información del usuario
            putString("username", userData.username)
            putString("email", userData.email)
            //putString("fullName", userData.fullName)

            // Si es un usuario que se logueó con la API, guardar el ID también
            userData.id?.let {
                putInt("id", it)
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
