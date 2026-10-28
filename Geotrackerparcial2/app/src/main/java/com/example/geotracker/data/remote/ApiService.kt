package com.example.geotracker.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// El header Authorization lo agrega AuthInterceptor, por eso no aparece aquí
interface ApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): ApiResponse<LoginData>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): ApiResponse<Usuario>

    @POST("api/ubicaciones")
    suspend fun guardarUbicacion(
        @Body request: UbicacionRequest
    ): ApiResponse<Ubicacion>

    @GET("api/ubicaciones")
    suspend fun obtenerUbicaciones(): ApiResponse<List<Ubicacion>>
}
