package com.example.cambiayanooficial2.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Base URL para la API
    private const val BASE_URL = "http://192.168.43.237:8000/api/"

    // Configuración de OkHttpClient (si es necesario añadir configuraciones adicionales, como interceptores, etc.)
    private val okHttpClient = OkHttpClient.Builder().build()

    // Crear una instancia de Retrofit
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Crear el ApiService
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
