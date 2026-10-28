package com.example.geotracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta Material 3 en tonos azules. Siempre oscura para que el texto
// se lea bien sobre el fondo degradado azul.
private val ColoresAzules = darkColorScheme(
    primary = Color(0xFF7CB8FF),
    onPrimary = Color(0xFF00244D),
    primaryContainer = Color(0xFF1E4FD8),
    onPrimaryContainer = Color(0xFFDCE8FF),
    inversePrimary = Color(0xFF1E4FD8),

    secondary = Color(0xFF8FD3FF),
    onSecondary = Color(0xFF00344F),
    secondaryContainer = Color(0xFF14396E),
    onSecondaryContainer = Color(0xFFD1E4FF),

    tertiary = Color(0xFF67E8F9),
    onTertiary = Color(0xFF00363D),

    background = Color(0xFF07122B),
    onBackground = Color(0xFFE6EEFF),
    surface = Color(0xFF0C1C3F),
    onSurface = Color(0xFFE6EEFF),
    surfaceVariant = Color(0xFF1B2D57),
    onSurfaceVariant = Color(0xFFB4C4E6),
    surfaceTint = Color(0xFF7CB8FF),

    surfaceContainerLowest = Color(0xFF061027),
    surfaceContainerLow = Color(0xFF0F2149),
    surfaceContainer = Color(0xFF132755),
    surfaceContainerHigh = Color(0xFF182E60),
    surfaceContainerHighest = Color(0xFF1E366C),

    outline = Color(0xFF6F84B3),
    outlineVariant = Color(0xFF2C3F6B),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun GeoTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColoresAzules,
        content = content
    )
}
