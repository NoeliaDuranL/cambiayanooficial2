package com.example.cambiayanooficial2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Usar Handler con Looper.getMainLooper() para retrasar la transición
        Handler(Looper.getMainLooper()).postDelayed({
            // Verificar en SharedPreferences si el usuario ya ha visto el onboarding
            // Recuperar SharedPreferences
            val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

            // Verificar si el usuario está logueado
            val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

            // Verificar si el onboarding fue completado
            val hasSeenOnboarding = sharedPref.getBoolean("hasSeenOnboarding", false)

            val intent = if (isLoggedIn) {
                // Si ya está logueado, ir directamente al HomeActivity
                Intent(this, MainActivity::class.java)
            } else {
                if (hasSeenOnboarding) {
                    // Si no está logueado, pero ya vio el onboarding, redirigir al Login
                    Intent(this, WelcomeActivity::class.java)
                } else {
                    // Si no está logueado y no ha visto el onboarding, redirigir al OnboardingActivity
                    Intent(this, OnboardingActivity::class.java)
                }
            }
            startActivity(intent)
            finish()  // Cierra SplashActivity
        }, 2000) // 2 segundos de retraso
    }
}
