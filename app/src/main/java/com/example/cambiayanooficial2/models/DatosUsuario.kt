package com.example.cambiayanooficial2.models

data class DatosUsuario(
    val id: Int? = null,          // Solo se usará cuando el login sea con la API
    val username: String?,       // Para almacenar el nombre de usuario (de Google o de la API)
    val email: String?,          // Para almacenar el correo electrónico (de Google o de la API)
    val fullName: String?        // Para almacenar el nombre completo (de Google o de la API)
)
