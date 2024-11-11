package com.example.cambiayanooficial2

data class PostProductoRequest(
    val nombreProducto: String,
    val descripcionProducto: String,
    val imagenProducto: String?, // URL de la imagen o null si no se proporciona
    val descripcionPost: String
)

