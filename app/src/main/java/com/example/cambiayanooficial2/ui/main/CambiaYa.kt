package com.example.cambiayanooficial2.ui.main


import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Configura el modo nocturno globalmente
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}
