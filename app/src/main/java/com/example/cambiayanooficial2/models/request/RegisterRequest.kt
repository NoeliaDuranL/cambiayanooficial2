package com.example.cambiayanooficial2.models.request

data class RegisterRequest(
    val usuario: String,
    val correo: String,
    val numero_celular: String,
    val contrasena: String,
    val id_persona: Int
)
