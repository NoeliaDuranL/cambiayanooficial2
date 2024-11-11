package com.example.cambiayanooficial2

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
