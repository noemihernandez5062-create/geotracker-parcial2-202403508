package com.example.geotracker.ui.captura

import com.example.geotracker.data.remote.Ubicacion

enum class PasoCaptura {
    INACTIVO,
    OBTENIENDO_GPS,
    ENVIANDO
}

data class CapturaState(
    val nombre: String = "",
    val descripcion: String = "",
    val paso: PasoCaptura = PasoCaptura.INACTIVO,

    // Última ubicación guardada con éxito
    val ubicacionGuardada: Ubicacion? = null,

    val mensaje: String? = null,
    val esError: Boolean = false
) {
    val cargando: Boolean get() = paso != PasoCaptura.INACTIVO
}
