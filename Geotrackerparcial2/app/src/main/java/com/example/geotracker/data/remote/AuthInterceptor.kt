package com.example.geotracker.data.remote

import com.example.geotracker.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

// Agrega "Authorization: Bearer <token>" a todas las peticiones cuando hay sesión
class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = TokenManager.obtenerToken()

        val request = if (token.isNullOrEmpty()) {
            chain.request()
        } else {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }

        return chain.proceed(request)
    }
}
