package com.example.cambiayanooficial2.network

data class RegisterRequest(
    val usuario: String,
    val correo: String,
    val contrasena: String,
    val id_persona: Int
)
