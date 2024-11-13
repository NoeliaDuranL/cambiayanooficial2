package com.example.cambiayanooficial2.models

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val token: String?  // Solo el token si lo necesitas para autenticación
)
