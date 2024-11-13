package com.example.cambiayanooficial2.network

import com.example.cambiayanooficial2.models.ApiResponse
import com.example.cambiayanooficial2.models.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    // Endpoint para el login
    @POST("login")
    suspend fun login(@Body requestBody: LoginRequest): ApiResponse

    // Endpoint para el registro
    @POST("register")
    suspend fun register(@Body requestBody: RegisterRequest): RegisterResponse
}