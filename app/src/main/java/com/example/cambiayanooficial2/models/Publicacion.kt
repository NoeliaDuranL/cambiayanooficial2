package com.example.cambiayanooficial2.models

data class Publicacion(
    val id: Int,
    val descripcion: String,
    val estado: Int,
    val producto: Producto,
    val usuario: Usuario,
    val created_at: String // Fecha de creación del post
)

data class Producto(
    val nombre: String,
    val descripcion: String,
    val imagen: String?
)

data class Usuario(
    val usuario: String
)

data class PostProductoRequest(
    val nombreProducto: String,
    val descripcionProducto: String,
    val imagenProducto: String?, // URL de la imagen o null si no se proporciona
    val descripcionPost: String
)