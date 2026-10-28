package com.example.geotracker.ui.login

data class LoginState(
    val correo: String = "",
    val password: String = "",
    val cargando: Boolean = false,
    val loginExitoso: Boolean = false,
    val error: String? = null,
    val aviso: String? = null
)
