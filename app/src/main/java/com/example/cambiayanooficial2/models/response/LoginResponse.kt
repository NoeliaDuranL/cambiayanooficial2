package com.example.cambiayanooficial2.models.response

import com.example.cambiayanooficial2.models.User

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: User? // Aquí es donde incluirías los datos del usuario
)
