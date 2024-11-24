package com.example.cambiayanooficial2.models

data class Notification(
    val id_notificacion: Int,
    val id_usuario: Int,
    val id_post: Int,
    val token: String?,
    val mensaje: String,
    val leido: Int
)