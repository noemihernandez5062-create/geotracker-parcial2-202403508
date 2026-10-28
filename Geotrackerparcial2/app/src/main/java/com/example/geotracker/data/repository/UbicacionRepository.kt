package com.example.geotracker.data.repository

import com.example.geotracker.data.remote.ApiResponse
import com.example.geotracker.data.remote.RetrofitClient
import com.example.geotracker.data.remote.Ubicacion
import com.example.geotracker.data.remote.UbicacionRequest

class UbicacionRepository {

    private val api = RetrofitClient.apiService

    suspend fun guardarUbicacion(
        nombre: String,
        descripcion: String,
        latitud: Double,
        longitud: Double
    ): ApiResponse<Ubicacion> {

        return api.guardarUbicacion(
            UbicacionRequest(
                nombre = nombre,
                descripcion = descripcion,
                latitud = latitud,
                longitud = longitud
            )
        )
    }

    suspend fun obtenerUbicaciones(): ApiResponse<List<Ubicacion>> {
        return api.obtenerUbicaciones()
    }
}
