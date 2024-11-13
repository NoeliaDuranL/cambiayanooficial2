package com.example.cambiayanooficial2.models

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: User? // Aquí es donde incluirías los datos del usuario
)
