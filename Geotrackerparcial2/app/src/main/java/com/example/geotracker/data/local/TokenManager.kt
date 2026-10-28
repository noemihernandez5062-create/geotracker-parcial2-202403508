package com.example.geotracker.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

// Guarda el token JWT que devuelve el login para enviarlo en las siguientes peticiones.
// Se guarda en SharedPreferences para que la sesión siga abierta al cerrar la app.
object TokenManager {

    private const val PREFS = "geotracker_sesion"
    private const val KEY_TOKEN = "token"
    private const val KEY_NOMBRE = "nombre"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    fun guardarSesion(token: String, nombre: String) {
        prefs.edit {
            putString(KEY_TOKEN, token)
            putString(KEY_NOMBRE, nombre)
        }
    }

    fun obtenerToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun obtenerNombre(): String = prefs.getString(KEY_NOMBRE, "") ?: ""

    fun haySesion(): Boolean = !obtenerToken().isNullOrEmpty()

    fun cerrarSesion() {
        prefs.edit { clear() }
    }
}
