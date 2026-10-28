package com.example.geotracker.ui.mapa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.geotracker.ui.theme.coloresBarra
import com.example.geotracker.data.remote.Ubicacion
import com.example.geotracker.ui.formatearCoordenadas
import com.example.geotracker.ui.formatearFecha
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

// Vista inicial: Ciudad de Guatemala (mientras llegan los datos)
private val POSICION_INICIAL = LatLng(14.6349, -90.5069)

// Estilo de mapa azul oscuro, a juego con el tema de la app
private const val ESTILO_MAPA_AZUL = """
[
  {"elementType":"geometry","stylers":[{"color":"#0c1c3f"}]},
  {"elementType":"labels.text.fill","stylers":[{"color":"#9fb7e6"}]},
  {"elementType":"labels.text.stroke","stylers":[{"color":"#061027"}]},
  {"featureType":"road","elementType":"geometry","stylers":[{"color":"#1b2d57"}]},
  {"featureType":"road.highway","elementType":"geometry","stylers":[{"color":"#1e4fd8"}]},
  {"featureType":"poi.park","elementType":"geometry","stylers":[{"color":"#123366"}]},
  {"featureType":"water","elementType":"geometry","stylers":[{"color":"#061433"}]},
  {"featureType":"poi","elementType":"labels","stylers":[{"visibility":"off"}]}
]
"""

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreen(
    onVolver: () -> Unit,
    viewModel: MapaViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(POSICION_INICIAL, 11f)
    }

    var mapaListo by remember { mutableStateOf(false) }

    // Centra la cámara para que se vean todos los marcadores
    LaunchedEffect(state.ubicaciones, mapaListo) {
        if (!mapaListo || state.ubicaciones.isEmpty()) return@LaunchedEffect

        val puntos = state.ubicaciones.map { LatLng(it.latitud, it.longitud) }.distinct()

        // Un solo punto (o varias ubicaciones con las mismas coordenadas):
        // los límites no tendrían tamaño y el mapa se acercaría al máximo
        if (puntos.size == 1) {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(puntos.first(), 16f))
        } else {
            val limites = LatLngBounds.builder().apply {
                puntos.forEach { include(it) }
            }.build()
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(limites, 150))
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                colors = coloresBarra(),
                title = { Text("Mapa de ubicaciones") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::cargarUbicaciones, enabled = !state.cargando) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
                cameraPositionState = cameraPositionState,
                properties = remember { MapProperties(mapStyleOptions = MapStyleOptions(ESTILO_MAPA_AZUL)) },
                onMapLoaded = { mapaListo = true }
            ) {
                state.ubicaciones.forEach { ubicacion ->
                    key(ubicacion.id) {
                        // Sin title/snippet y con onClick = true, Google Maps no abre su
                        // InfoWindow predeterminado; en su lugar se muestra el AlertDialog.
                        Marker(
                            state = rememberMarkerState(
                                position = LatLng(ubicacion.latitud, ubicacion.longitud)
                            ),
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                            onClick = {
                                viewModel.seleccionarUbicacion(ubicacion)
                                true
                            }
                        )
                    }
                }
            }

            if (state.cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // MENSAJE: error o sin ubicaciones
            val mensaje = when {
                state.cargando -> null
                state.error != null -> state.error
                state.ubicaciones.isEmpty() -> "Aún no has guardado ubicaciones. Captura una para verla en el mapa."
                else -> null
            }

            mensaje?.let {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = null)
                        Text(
                            text = it,
                            color = if (state.error != null) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }
    }

    // ALERTDIALOG DE MATERIAL 3: su visibilidad y datos vienen del ViewModel
    if (state.mostrarDialogo) {
        state.ubicacionSeleccionada?.let { ubicacion ->
            DialogoUbicacion(
                ubicacion = ubicacion,
                onCerrar = viewModel::cerrarDialogo
            )
        }
    }
}

@Composable
private fun DialogoUbicacion(
    ubicacion: Ubicacion,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        icon = { Icon(Icons.Filled.LocationOn, contentDescription = null) },
        title = { Text(ubicacion.nombre) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DatoDialogo("Descripción", ubicacion.descripcion)
                DatoDialogo("Latitud", String.format(java.util.Locale.US, "%.6f", ubicacion.latitud))
                DatoDialogo("Longitud", String.format(java.util.Locale.US, "%.6f", ubicacion.longitud))
                DatoDialogo("Coordenadas", formatearCoordenadas(ubicacion.latitud, ubicacion.longitud))
                if (!ubicacion.fechaCreacion.isNullOrBlank()) {
                    DatoDialogo("Fecha", formatearFecha(ubicacion.fechaCreacion))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun DatoDialogo(etiqueta: String, valor: String) {
    Column {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = valor, style = MaterialTheme.typography.bodyLarge)
    }
}
