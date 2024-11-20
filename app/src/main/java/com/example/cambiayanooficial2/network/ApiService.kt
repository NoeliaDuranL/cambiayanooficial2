package com.example.cambiayanooficial2.network

import com.example.cambiayanooficial2.models.PostProductoRequest
import com.example.cambiayanooficial2.models.Publicacion
import com.example.cambiayanooficial2.models.request.LoginRequest
import com.example.cambiayanooficial2.models.request.Product
import com.example.cambiayanooficial2.models.request.RegisterRequest
import com.example.cambiayanooficial2.models.response.ApiResponse
import com.example.cambiayanooficial2.models.response.ImageUploadResponse
import com.example.cambiayanooficial2.models.response.ProductResponse
import com.example.cambiayanooficial2.models.response.RegisterResponse
import com.example.cambiayanooficial2.models.response.UploadResponse
import com.example.cambiayanooficial2.models.response.responseProducto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    // Endpoints para Login y Registro
    @POST("login")
    suspend fun login(@Body requestBody: LoginRequest): ApiResponse

    @POST("register")
    suspend fun register(@Body requestBody: RegisterRequest): RegisterResponse

    // Endpoints para productos y publicaciones
    @GET("post")
    suspend fun getPosts(): List<Publicacion>

    @POST("post-producto")
    suspend fun crearProductoYPost(@Body producto: PostProductoRequest): Publicacion

    // Endpoint para crear un producto
    @POST("products")
    suspend fun createProduct(@Body product: Product): Response<ProductResponse>

    // Endpoint para subir la imagen
    @Multipart
    @POST("products/uploadImage")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("productId") productId: RequestBody
    ): Call<ImageUploadResponse>
}
