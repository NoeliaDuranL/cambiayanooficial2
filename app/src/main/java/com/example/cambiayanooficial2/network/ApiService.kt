package com.example.cambiayanooficial2.network

import com.example.cambiayanooficial2.models.PostProductoRequest
import com.example.cambiayanooficial2.models.Publicacion
import com.example.cambiayanooficial2.models.request.LoginRequest
import com.example.cambiayanooficial2.models.request.RegisterRequest
import com.example.cambiayanooficial2.models.response.ApiResponse
import com.example.cambiayanooficial2.models.response.RegisterResponse
import com.example.cambiayanooficial2.models.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    // Endpoints para Login y Registro
    @POST("login")
    suspend fun login(@Body requestBody: LoginRequest): Response<ApiResponse>

    @POST("register")
    suspend fun register(@Body requestBody: RegisterRequest): RegisterResponse

    // Endpoints para productos y publicaciones
    @GET("post")
    suspend fun getPosts(): List<Publicacion>

    @POST("post-producto")
    suspend fun crearProductoYPost(@Body producto: PostProductoRequest): Publicacion

    // Endpoint para subir la imagen
    @Multipart
    @POST("upload-image")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("id_producto") idProducto: RequestBody
    ): Call<UploadResponse>
}
