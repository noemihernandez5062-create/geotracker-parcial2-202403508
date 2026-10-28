package com.example.geotracker.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

// "parque central" → "Parque central"
fun String.conMayusculaInicial(): String = replaceFirstChar { it.uppercase() }

// Muestra la primera letra en mayúscula sin modificar lo que el teclado está escribiendo
// (cambiar el texto en cada tecla hace que el teclado pierda letras).
// El ViewModel aplica la mayúscula real al guardar.
object MayusculaInicial : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText =
        TransformedText(AnnotatedString(text.text.conMayusculaInicial()), OffsetMapping.Identity)
}

fun formatearCoordenadas(latitud: Double, longitud: Double): String =
    String.format(Locale.US, "%.6f, %.6f", latitud, longitud)

// El backend envía la fecha en UTC, por ejemplo "2026-10-27T20:15:30.123Z";
// se muestra en la hora local del teléfono.
fun formatearFecha(fechaIso: String?): String {
    if (fechaIso.isNullOrBlank()) return ""

    return try {
        val entrada = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val salida = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        salida.format(entrada.parse(fechaIso)!!)
    } catch (e: Exception) {
        fechaIso.take(10)
    }
}
