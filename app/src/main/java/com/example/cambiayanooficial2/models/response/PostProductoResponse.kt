package com.example.cambiayanooficial2.models.response

import com.example.cambiayanooficial2.models.Producto
import com.example.cambiayanooficial2.models.Usuario

data class PostProductoResponse(
    val idPost: Int,
    val idProducto: Int,
    val idUsuario: Int,
    val descripcion: String,
    val estado: Int,
    val producto: Producto, // Incluye datos del producto creado
    val usuario: Usuario    // Incluye datos del usuario
)
