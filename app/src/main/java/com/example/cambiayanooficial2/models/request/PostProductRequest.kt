package com.example.cambiayanooficial2.models.request

// Datos que se enviarán en la solicitud POST
data class PostProductRequest(
    val name: String,
    val image_url: String,
    val description: String,
    val user_id: Int // Agregar el ID del usuario al objeto
)