package com.example.geotracker.ui.registro

data class RegistroState(
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val password: String = "",
    val confirmarPassword: String = "",
    val cargando: Boolean = false,
    val registroExitoso: Boolean = false,
    val error: String? = null
)
