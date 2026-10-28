package com.example.geotracker.ui.lista

import com.example.geotracker.data.remote.Ubicacion

data class ListaState(
    val cargando: Boolean = false,
    val ubicaciones: List<Ubicacion> = emptyList(),
    val error: String? = null
)
