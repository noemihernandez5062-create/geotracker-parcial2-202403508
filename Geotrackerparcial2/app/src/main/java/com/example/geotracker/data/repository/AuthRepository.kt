package com.example.geotracker.data.repository

import com.example.geotracker.data.local.TokenManager
import com.example.geotracker.data.remote.ApiResponse
import com.example.geotracker.data.remote.LoginData
import com.example.geotracker.data.remote.LoginRequest
import com.example.geotracker.data.remote.RegisterRequest
import com.example.geotracker.data.remote.RetrofitClient
import com.example.geotracker.data.remote.Usuario

class AuthRepository {

    private val api = RetrofitClient.apiService

    // Si el login es correcto guarda el token para las siguientes peticiones
    suspend fun login(correo: String, password: String): ApiResponse<LoginData> {
        val respuesta = api.login(LoginRequest(correo = correo, password = password))

        if (respuesta.success && respuesta.data != null) {
            TokenManager.guardarSesion(
                token = respuesta.data.token,
                nombre = respuesta.data.usuario.nombre
            )
        }

        return respuesta
    }

    suspend fun register(
        nombre: String,
        correo: String,
        telefono: String?,
        password: String
    ): ApiResponse<Usuario> {

        return api.register(
            RegisterRequest(
                nombre = nombre,
                correo = correo,
                telefono = telefono,
                password = password
            )
        )
    }

    fun nombreUsuario(): String = TokenManager.obtenerNombre()

    fun cerrarSesion() {
        TokenManager.cerrarSesion()
    }
}
