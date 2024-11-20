package com.example.cambiayanooficial2.network

import android.bluetooth.BluetoothHidDevice
import com.example.cambiayanooficial2.models.response.responseProducto
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Base URL para la API
    private const val BASE_URL = "http://192.168.100.4:8000/api/"

    // Configuración de OkHttpClient (si es necesario añadir configuraciones adicionales, como interceptores, etc.)
    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }


}
