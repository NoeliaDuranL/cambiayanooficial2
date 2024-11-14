package com.example.cambiayanooficial2.models.response

import com.example.cambiayanooficial2.models.User

data class ApiResponse(
    val success: Boolean,   // Indica si el login fue exitoso
    val message: String,    // Mensaje de la respuesta
    val token: String?,     // Token de autenticación
    val user: User?         // Objeto de tipo User (los datos del usuario)
)