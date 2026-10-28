package com.example.geotracker.data.remote

// El backend responde siempre { success, message, data }
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

// ---------- Login ----------

data class LoginRequest(
    val correo: String,
    val password: String
)

data class Usuario(
    val id: Int,
    val nombre: String,
    val correo: String
)

data class LoginData(
    val token: String,
    val usuario: Usuario
)

// ---------- Registro ----------

data class RegisterRequest(
    val nombre: String,
    val correo: String,
    val telefono: String?,
    val password: String
)

// ---------- Ubicaciones ----------

data class UbicacionRequest(
    val nombre: String,
    val descripcion: String,
    val latitud: Double,
    val longitud: Double
)

data class Ubicacion(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val latitud: Double,
    val longitud: Double,
    val fechaCreacion: String?
)
