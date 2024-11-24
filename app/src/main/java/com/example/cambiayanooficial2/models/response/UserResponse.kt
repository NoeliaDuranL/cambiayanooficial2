package com.example.cambiayanooficial2.models.response

data class UserResponse(
    val message: String,
    val success: Boolean,
    val id_usuario: Int,
    val usuario: String,
    val correo: String,
    val nombre: String,
    val apellido: String
)