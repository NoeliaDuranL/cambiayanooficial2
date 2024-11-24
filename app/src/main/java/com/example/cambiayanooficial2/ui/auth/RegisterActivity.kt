package com.example.cambiayanooficial2.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.example.cambiayanooficial2.models.DatosUsuario
import com.example.cambiayanooficial2.models.request.RegisterRequest
import com.example.cambiayanooficial2.network.ApiClient
import com.example.cambiayanooficial2.ui.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var numberEditText: EditText  //Agregnado numero
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var darkOverlay: View
    private lateinit var googleSignInButton: Button
    private lateinit var auth: FirebaseAuth

    private val RC_SIGN_IN = 1001  // Código para el flujo de Google Sign-In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicialización de las vistas
        usernameEditText = findViewById(R.id.username)
        emailEditText = findViewById(R.id.email)
        numberEditText = findViewById(R.id.number) //Numero del Usuario
        passwordEditText = findViewById(R.id.password)
        registerButton = findViewById(R.id.register_button)
        loadingProgressBar = findViewById(R.id.loading)
        darkOverlay = findViewById(R.id.dark_overlay)
        googleSignInButton = findViewById(R.id.google_register_button)



        auth = FirebaseAuth.getInstance()

        // Configuración de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))  // Asegúrate de tener el Client ID en `strings.xml`
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Google Sign-In Button
        googleSignInButton.setOnClickListener {
            auth.signOut() // Cerrar la sesión actual de Firebase (esto es lo que permitirá elegir otro correo)


            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut().addOnCompleteListener {
                // Ahora el usuario debería ser capaz de elegir una nueva cuenta de Google
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }
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

        // Validación en tiempo real con TextWatcher
        numberEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateNumber()
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
            val number = numberEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (validateUsername() && validateEmail() && validatePassword()) {
                // Ejecutar la llamada en segundo plano utilizando coroutines
                registerUser(username, email, number, password)
            } else {
                Toast.makeText(this, "Por favor completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task?.getResult(ApiException::class.java)
                account?.let {
                    firebaseAuthWithGoogle(it.idToken!!)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {
                    val userData = RegisterRequest(
                        usuario = it.displayName ?: "",
                        correo = it.email ?: "",
                        numero_celular = "",
                        contrasena = idToken,
                        id_persona = 1
                    )
                    registerUserWithGoogle(userData)
                }
            } else {
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUserWithGoogle(userData: RegisterRequest) {
        lifecycleScope.launch {
            try {
                setLoading(true)
                val apiService = ApiClient.apiService
                val response = apiService.register(userData)

                setLoading(false)
                if (response.success) {
                    Toast.makeText(this@RegisterActivity, "Registro exitoso con Google", Toast.LENGTH_SHORT).show()
                    val respuesta = response
                    val saveData = DatosUsuario(
                        username = userData.usuario,
                        email = userData.correo,
                        id = response.id_usuario,
                        fullName = "",
                    )


                    Log.e("Datos del usaurio de google", "Datos GOOOGLE: ${saveData}")
                    Log.e("Datos del usaurio de google", "Datos GOOOGLE: ${respuesta}")
                    saveUserData(saveData)
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Error en el registro: ${response.message}", Toast.LENGTH_SHORT).show()
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



    private fun validateUsername(): Boolean {
        val username = usernameEditText.text.toString().trim()
        return if (username.isEmpty()) {
            usernameEditText.error = "El nombre de usuario es requerido"
            false
        } else if (username.length < 8) {
            usernameEditText.error = "El nombre de usuario debe tener al menos 8 caracteres"
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

    private fun validateNumber(): Boolean {
        val number = numberEditText.text.toString().trim()
        return if (number.isEmpty()) {
            numberEditText.error = "El telefono es requerido"
            false
        } else if (number.length < 8) {
            numberEditText.error = "El telefono debe tener al menos 8 caracteres"
            false
        } else {
            numberEditText.error = null // Limpiar error si es válido
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

    private fun registerUser(username: String, email: String, number: String, password: String) {
        val registerRequest = RegisterRequest(
            id_persona = 1,  // Cambia a tu ID real
            usuario = username,
            numero_celular = number,
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

    private fun saveUserData(userData: DatosUsuario) {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isLoggedIn", true)

            // Guardar la información del usuario
            putString("username", userData.username)
            putString("email", userData.email)
            putString("fullName", userData.fullName)

            // Si es un usuario que se logueó con la API, guardar el ID también
            userData.id?.let {
                putInt("id", it)
            }

            apply()
        }
    }

}
