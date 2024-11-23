package com.example.cambiayanooficial2.models.response

import com.example.cambiayanooficial2.models.Post
import com.example.cambiayanooficial2.models.Producto

data class PostProductoResponse(
    val success: Boolean,
    val message: String, // Nuevo campo para el mensaje
    val producto: Producto, // Producto creado
    val post: Post // Post asociado
)
