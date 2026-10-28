package com.example.geotracker.ui.dashboard

import androidx.lifecycle.ViewModel
import com.example.geotracker.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DashboardState(
    val nombreUsuario: String = ""
)

class DashboardViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _state = MutableStateFlow(DashboardState(nombreUsuario = repository.nombreUsuario()))
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun cerrarSesion() {
        repository.cerrarSesion()
    }
}
