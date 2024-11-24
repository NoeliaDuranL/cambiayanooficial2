package com.example.cambiayanooficial2.models.response

data class RegisterResponse(
    val id_usuario: Int,
    val success: Boolean,
    val message: String,
    val token: String?  // Solo el token si lo necesitas para autenticación
)
