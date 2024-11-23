package com.example.cambiayanooficial2.models.response

import com.example.cambiayanooficial2.models.Publicacion

data class PostResponse(
    val success: Boolean,    // Indica si la solicitud de publicaciones fue exitosa
    val message: String?,    // Mensaje adicional de la respuesta (opcional)
    val data: List<Publicacion>  // Lista de publicaciones
)