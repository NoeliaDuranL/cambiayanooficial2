package com.example.cambiayanooficial2

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

interface ApiService {
    @GET("post") // Asegúrate de que este endpoint coincide con el de tu backend
    suspend fun getPosts(): List<Publicacion>
    @POST("post-producto") // Endpoint para crear producto y post
    suspend fun crearProductoYPost(@Body producto: PostProductoRequest): Publicacion
}

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
