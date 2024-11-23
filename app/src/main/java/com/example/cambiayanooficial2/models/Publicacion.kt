package com.example.cambiayanooficial2.models

data class Publicacion(
    val id_post: Int,             // ID del post
    val id_producto: Int,         // ID del producto asociado
    val id_usuario: Int,          // ID del usuario que publica
    val descripcion: String?,     // Descripción del post (opcional según tu JSON)
    val estado: Int,              // Estado del post
    val producto: Producto,       // Producto asociado al post
    val usuario: Usuario,         // Usuario que realizó la publicación
    val created_at: String        // Fecha de creación
)



data class Producto(
    val id_producto: Int, // ID del producto
    val nombre: String, // Nombre del producto
    val descripcion: String, // Descripción del producto
    val imagen: String?, // URL de la imagen (puede ser null)
    val created_at: String, // Fecha de creación
    val updated_at: String // Fecha de última actualización
)

data class Post(
    val id_post: Int, // ID del post
    val id_producto: Int, // ID del producto asociado
    val id_usuario: String, // ID del usuario (String en el JSON)
    val created_at: String, // Fecha de creación
    val updated_at: String // Fecha de última actualización
)

data class Usuario(
    val id_usuario: Int,          // ID del usuario
    val usuario: String,          // Nombre de usuario
    val correo: String,
    val numero_celular: String, //numero del suario
    val estado: Int,              // Estado del usuario
    val created_at: String?,      // Fecha de creación del usuario
    val updated_at: String?       // Fecha de última actualización del usuario
)

//data class PostProductoRequest(
//    val nombre: String,   // Nombre del producto
//    val descripcion: String, // Descripción del producto
//    val image_url: String?,
//    val id_usuario: Int// URL de la imagen o null si no se proporciona// Descripción del post
//)


