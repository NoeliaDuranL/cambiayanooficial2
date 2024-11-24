package com.example.cambiayanooficial2.models.request

data class NotificationRequest(
    val id_usuario: Int,
    val id_remitente: Int,
    val id_post: Int,
    val token: String?,
    val mensaje: String?
)