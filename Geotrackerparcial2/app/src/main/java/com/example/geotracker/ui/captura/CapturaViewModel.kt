package com.example.geotracker.ui.captura

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.geotracker.data.remote.mensajeUsuario
import com.example.geotracker.data.repository.LocationRepository
import com.example.geotracker.data.repository.UbicacionRepository
import com.example.geotracker.ui.conMayusculaInicial
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// AndroidViewModel porque el LocationRepository necesita un Context
class CapturaViewModel(application: Application) : AndroidViewModel(application) {

    private val ubicacionRepository = UbicacionRepository()
    private val locationRepository = LocationRepository(application)

    private val _state = MutableStateFlow(CapturaState())
    val state: StateFlow<CapturaState> = _state.asStateFlow()

    fun onNombreChange(valor: String) {
        _state.update { it.copy(nombre = valor, mensaje = null) }
    }

    fun onDescripcionChange(valor: String) {
        _state.update { it.copy(descripcion = valor, mensaje = null) }
    }

    // La pantalla valida el formulario con esto antes de pedir el permiso
    fun formularioValido(): Boolean {
        val actual = _state.value

        if (actual.nombre.isBlank() || actual.descripcion.isBlank()) {
            mostrarError("Escribe un nombre y una descripción")
            return false
        }
        return true
    }

    fun onPermisoDenegado() {
        mostrarError("Se necesita el permiso de ubicación para capturar tu posición")
    }

    // Se llama cuando ya se tiene el permiso de ubicación
    fun capturarYGuardar() {
        if (!formularioValido() || _state.value.cargando) return

        _state.update { it.copy(paso = PasoCaptura.OBTENIENDO_GPS, mensaje = null) }

        viewModelScope.launch {
            try {
                // 1. Ubicación GPS actual
                val ubicacionGps = locationRepository.obtenerUbicacionActual()

                if (ubicacionGps == null) {
                    mostrarError("No se pudo obtener la ubicación. Verifica que el GPS esté activado.")
                    return@launch
                }

                // 2. Enviar al backend
                _state.update { it.copy(paso = PasoCaptura.ENVIANDO) }

                val actual = _state.value
                val respuesta = ubicacionRepository.guardarUbicacion(
                    nombre = actual.nombre.trim().conMayusculaInicial(),
                    descripcion = actual.descripcion.trim().conMayusculaInicial(),
                    latitud = ubicacionGps.latitude,
                    longitud = ubicacionGps.longitude
                )

                if (respuesta.success && respuesta.data != null) {
                    // Limpia el formulario y muestra lo que se guardó
                    _state.value = CapturaState(
                        ubicacionGuardada = respuesta.data,
                        mensaje = respuesta.message,
                        esError = false
                    )
                } else {
                    mostrarError(respuesta.message)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                mostrarError(e.mensajeUsuario())
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        _state.update {
            it.copy(paso = PasoCaptura.INACTIVO, mensaje = mensaje, esError = true)
        }
    }
}
