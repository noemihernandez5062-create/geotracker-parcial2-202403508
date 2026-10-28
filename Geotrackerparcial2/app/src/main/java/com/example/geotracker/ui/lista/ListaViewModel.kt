package com.example.geotracker.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geotracker.data.remote.mensajeUsuario
import com.example.geotracker.data.repository.UbicacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListaViewModel : ViewModel() {

    private val repository = UbicacionRepository()

    private val _state = MutableStateFlow(ListaState())
    val state: StateFlow<ListaState> = _state.asStateFlow()

    init {
        cargarUbicaciones()
    }

    fun cargarUbicaciones() {
        _state.update { it.copy(cargando = true, error = null) }

        viewModelScope.launch {
            try {
                val respuesta = repository.obtenerUbicaciones()

                _state.update {
                    it.copy(
                        cargando = false,
                        ubicaciones = respuesta.data ?: emptyList(),
                        error = if (respuesta.success) null else respuesta.message
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(cargando = false, error = e.mensajeUsuario()) }
            }
        }
    }
}
