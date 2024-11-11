package com.example.cambiayanooficial2

data class PostProductoResponse(
    val idPost: Int,
    val idProducto: Int,
    val idUsuario: Int,
    val descripcion: String,
    val estado: Int,
    val producto: Producto, // Incluye datos del producto creado
    val usuario: Usuario    // Incluye datos del usuario
)
