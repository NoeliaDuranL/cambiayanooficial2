package com.example.cambiayanooficial2.network

import com.example.cambiayanooficial2.models.request.LoginRequest
import com.example.cambiayanooficial2.models.request.NotificationRequest
import com.example.cambiayanooficial2.models.request.PostProductRequest
import com.example.cambiayanooficial2.models.request.Product
import com.example.cambiayanooficial2.models.request.RegisterRequest
import com.example.cambiayanooficial2.models.response.ApiResponse
import com.example.cambiayanooficial2.models.response.NotificationResponse
import com.example.cambiayanooficial2.models.response.PostResponse
import com.example.cambiayanooficial2.models.response.ProductResponse
import com.example.cambiayanooficial2.models.response.RegisterResponse
import com.example.cambiayanooficial2.models.response.UserResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    // Endpoint para crear un producto
    @POST("products")
    suspend fun createProduct(@Body product: Product): Response<ProductResponse>


    // Endpoints para Login y Registro
    @POST("login")
    suspend fun login(@Body requestBody: LoginRequest): Response<ApiResponse>
    // Definir la función en tu interfaz de servicio de Retrofit

    @POST("id_user")
    suspend fun getUserIdByEmail(@Query("correo") email: String): Response<UserResponse>



    @POST("register")
    suspend fun register(@Body requestBody: RegisterRequest): RegisterResponse

    // Endpoints para productos y publicaciones
    @GET("post")
    suspend fun getPosts(): PostResponse


    @POST("post-producto")
    fun postProduct(@Body request: PostProductRequest): Call<ResponseBody>


    // Endpoint para obtener las notificaciones
    @GET("notificacion")
    suspend fun getNotifications(@Query("id_usuario") id_usuario: Int): NotificationResponse


    // Endpoint para enviar la notificación de "Me interesa"
    @POST("notificacion/meinteresa")
    suspend fun enviarNotificacionMeInteresa(@Body notificacionRequest: NotificationRequest): NotificationResponse

}
