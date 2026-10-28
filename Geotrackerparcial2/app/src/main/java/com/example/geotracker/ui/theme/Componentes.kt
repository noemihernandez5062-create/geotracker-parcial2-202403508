package com.example.geotracker.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Fondo de todas las pantallas: azul marino arriba → azul brillante abajo
val FondoDegradado = Brush.verticalGradient(
    listOf(
        Color(0xFF061433),
        Color(0xFF0D2A6B),
        Color(0xFF1A4FB8)
    )
)

// Botones e íconos destacados: azul rey → azul cielo
val DegradadoAcento = Brush.horizontalGradient(
    listOf(
        Color(0xFF1D4ED8),
        Color(0xFF38BDF8)
    )
)

private val DegradadoDeshabilitado = Brush.horizontalGradient(
    listOf(
        Color(0xFF2C3F6B),
        Color(0xFF3A4E7A)
    )
)

// Tarjetas semitransparentes para que se note el degradado detrás
@Composable
fun coloresTarjeta(): CardColors = CardDefaults.cardColors(
    containerColor = Color(0xFF0E2350).copy(alpha = 0.82f),
    contentColor = MaterialTheme.colorScheme.onSurface
)

// Barra superior transparente sobre el degradado
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun coloresBarra(): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
    containerColor = Color.Transparent,
    scrolledContainerColor = Color.Transparent,
    titleContentColor = Color.White,
    navigationIconContentColor = Color.White,
    actionIconContentColor = Color.White
)

// Botón principal de Material 3 relleno con el degradado azul
@Composable
fun BotonDegradado(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    cargando: Boolean = false,
    habilitado: Boolean = true
) {
    val activo = habilitado && !cargando

    Button(
        onClick = onClick,
        enabled = activo,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        ),
        contentPadding = PaddingValues(),
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (activo) DegradadoAcento else DegradadoDeshabilitado),
            contentAlignment = Alignment.Center
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icono != null) {
                        Icon(icono, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(texto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// Ícono blanco dentro de un círculo con el degradado azul
@Composable
fun IconoDegradado(
    icono: ImageVector,
    tamano: Dp = 52.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(DegradadoAcento),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(tamano * 0.55f)
        )
    }
}
