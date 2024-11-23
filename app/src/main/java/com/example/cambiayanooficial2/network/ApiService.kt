package com.example.cambiayanooficial2.network

import com.example.cambiayanooficial2.models.request.LoginRequest
import com.example.cambiayanooficial2.models.request.PostProductRequest
import com.example.cambiayanooficial2.models.request.Product
import com.example.cambiayanooficial2.models.request.RegisterRequest
import com.example.cambiayanooficial2.models.response.ApiResponse
import com.example.cambiayanooficial2.models.response.PostResponse
import com.example.cambiayanooficial2.models.response.ProductResponse
import com.example.cambiayanooficial2.models.response.RegisterResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    // Endpoint para crear un producto
    @POST("products")
    suspend fun createProduct(@Body product: Product): Response<ProductResponse>


    // Endpoints para Login y Registro
    @POST("login")
    suspend fun login(@Body requestBody: LoginRequest): Response<ApiResponse>


    @POST("register")
    suspend fun register(@Body requestBody: RegisterRequest): RegisterResponse

    // Endpoints para productos y publicaciones
    @GET("post")
    suspend fun getPosts(): PostResponse


    @POST("post-producto")
    fun postProduct(@Body request: PostProductRequest): Call<ResponseBody>
}
