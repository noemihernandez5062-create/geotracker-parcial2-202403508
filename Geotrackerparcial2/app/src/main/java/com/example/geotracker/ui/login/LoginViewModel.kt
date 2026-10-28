package com.example.geotracker.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geotracker.data.remote.mensajeServidor
import com.example.geotracker.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onCorreoChange(valor: String) {
        _state.update { it.copy(correo = valor, error = null) }
    }

    fun onPasswordChange(valor: String) {
        _state.update { it.copy(password = valor, error = null) }
    }

    // Al volver del registro: deja el correo escrito y avisa que ya puede entrar
    fun onCuentaCreada(correo: String) {
        _state.update {
            it.copy(correo = correo, password = "", error = null, aviso = "Cuenta creada. Ingresa tu contraseña para entrar.")
        }
    }

    fun iniciarSesion() {
        val correo = _state.value.correo.trim()
        val password = _state.value.password

        if (correo.isEmpty() || password.isEmpty()) {
            _state.update { it.copy(error = "Ingresa tu correo y contraseña") }
            return
        }

        _state.update { it.copy(cargando = true, error = null, aviso = null) }

        viewModelScope.launch {
            try {
                val respuesta = repository.login(correo, password)

                if (respuesta.success && respuesta.data != null) {
                    _state.update { it.copy(cargando = false, loginExitoso = true) }
                } else {
                    _state.update { it.copy(cargando = false, error = respuesta.message) }
                }

            } catch (e: HttpException) {
                // 400 datos faltantes, 401 credenciales incorrectas, 500 error interno
                val mensaje = if (e.code() == 401) "Correo o contraseña incorrectos" else e.mensajeServidor()
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
