package com.example.geotracker.ui.registro

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geotracker.data.remote.mensajeServidor
import com.example.geotracker.data.repository.AuthRepository
import com.example.geotracker.ui.conMayusculaInicial
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RegistroViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _state = MutableStateFlow(RegistroState())
    val state: StateFlow<RegistroState> = _state.asStateFlow()

    fun onNombreChange(valor: String) = _state.update { it.copy(nombre = valor, error = null) }

    fun onCorreoChange(valor: String) = _state.update { it.copy(correo = valor, error = null) }

    fun onTelefonoChange(valor: String) {
        if (valor.all { it.isDigit() }) _state.update { it.copy(telefono = valor, error = null) }
    }

    fun onPasswordChange(valor: String) = _state.update { it.copy(password = valor, error = null) }

    fun onConfirmarPasswordChange(valor: String) =
        _state.update { it.copy(confirmarPassword = valor, error = null) }

    fun registrar() {
        val actual = _state.value
        val correo = actual.correo.trim()

        val error = when {
            actual.nombre.isBlank() || correo.isEmpty() || actual.password.isEmpty() ->
                "Nombre, correo y contraseña son obligatorios"
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> "El correo no es válido"
            actual.password.length < 5 -> "La contraseña debe tener al menos 5 caracteres"
            actual.password != actual.confirmarPassword -> "Las contraseñas no coinciden"
            else -> null
        }

        if (error != null) {
            _state.update { it.copy(error = error) }
            return
        }

        _state.update { it.copy(cargando = true, error = null) }

        viewModelScope.launch {
            try {
                val respuesta = repository.register(
                    nombre = actual.nombre.trim().conMayusculaInicial(),
                    correo = correo,
                    telefono = actual.telefono.ifBlank { null },
                    password = actual.password
                )

                if (respuesta.success) {
                    _state.update { it.copy(cargando = false, registroExitoso = true) }
                } else {
                    _state.update { it.copy(cargando = false, error = respuesta.message) }
                }

            } catch (e: HttpException) {
                // 400 datos inválidos, 409 correo ya registrado
                val mensaje = if (e.code() == 409) "Ese correo ya está registrado" else e.mensajeServidor()
                _state.update { it.copy(cargando = false, error = mensaje) }

            } catch (e: IOException) {
                _state.update {
                    it.copy(cargando = false, error = "No se pudo conectar con el servidor. Revisa tu conexión a internet.")
                }

            } catch (e: Exception) {
                _state.update { it.copy(cargando = false, error = e.message ?: "Ocurrió un error inesperado") }
            }
        }
    }
}
