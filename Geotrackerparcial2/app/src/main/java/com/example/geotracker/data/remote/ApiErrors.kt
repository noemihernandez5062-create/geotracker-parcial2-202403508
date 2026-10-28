package com.example.geotracker.data.remote

import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

private data class ErrorResponse(
    val success: Boolean?,
    val message: String?
)

// Con códigos 4xx/5xx Retrofit lanza HttpException; el mensaje del backend
// viene en el errorBody con el formato { success, message, data }.
fun HttpException.mensajeServidor(): String {
    val cuerpo = response()?.errorBody()?.string()

    val mensaje = try {
        Gson().fromJson(cuerpo, ErrorResponse::class.java)?.message
    } catch (e: Exception) {
        null
    }

    return if (mensaje.isNullOrBlank()) "Error del servidor (${code()})" else mensaje
}

// Mensaje para mostrar al usuario según el tipo de error
fun Throwable.mensajeUsuario(): String = when (this) {
    is HttpException -> if (code() == 401) {
        "Tu sesión venció. Cierra sesión y vuelve a entrar."
    } else {
        mensajeServidor()
    }
    is IOException -> "No se pudo conectar con el servidor. Revisa tu conexión a internet."
    else -> message ?: "Ocurrió un error inesperado"
}
