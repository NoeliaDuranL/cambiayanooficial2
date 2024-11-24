package com.example.cambiayanooficial2.models.response

import com.example.cambiayanooficial2.models.Notification

data class NotificationResponse(
    val success: Boolean,
    val message: String,
    val data: List<Notification> // Listado de notificaciones
)


