package com.example.geotracker.ui.mapa

import com.example.geotracker.data.remote.Ubicacion

data class MapaState(
    val cargando: Boolean = false,
    val ubicaciones: List<Ubicacion> = emptyList(),
    val error: String? = null,

    // Ubicación cuyo marcador se tocó; si no es null se muestra el AlertDialog
    val ubicacionSeleccionada: Ubicacion? = null
) {
    val mostrarDialogo: Boolean get() = ubicacionSeleccionada != null
}
