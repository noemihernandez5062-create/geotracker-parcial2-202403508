package com.example.geotracker.ui.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.geotracker.ui.theme.IconoDegradado
import com.example.geotracker.ui.theme.coloresBarra
import com.example.geotracker.ui.theme.coloresTarjeta
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import com.example.geotracker.data.remote.Ubicacion
import com.example.geotracker.ui.formatearCoordenadas
import com.example.geotracker.ui.formatearFecha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    onVolver: () -> Unit,
    onVerMapa: () -> Unit,
    viewModel: ListaViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                colors = coloresBarra(),
                title = { Text("Mis ubicaciones") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onVerMapa) {
                        Icon(Icons.Filled.Place, contentDescription = "Ver en el mapa")
                    }
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
            when {
                // INDICADOR DE CARGA
                state.cargando -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                state.error != null -> MensajeCentrado(
                    texto = state.error!!,
                    esError = true,
                    onReintentar = viewModel::cargarUbicaciones,
                    modifier = Modifier.align(Alignment.Center)
                )

                state.ubicaciones.isEmpty() -> MensajeCentrado(
                    texto = "Aún no has guardado ubicaciones.\nCaptura una desde el inicio.",
                    esError = false,
                    onReintentar = viewModel::cargarUbicaciones,
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "${state.ubicaciones.size} ubicaciones guardadas",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(state.ubicaciones, key = { it.id }) { ubicacion ->
                        UbicacionCard(ubicacion)
                    }
                }
            }
        }
    }
}

@Composable
private fun UbicacionCard(ubicacion: Ubicacion) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = coloresTarjeta(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconoDegradado(icono = Icons.Filled.LocationOn, tamano = 44.dp)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(ubicacion.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(ubicacion.descripcion, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = formatearCoordenadas(ubicacion.latitud, ubicacion.longitud),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = formatearFecha(ubicacion.fechaCreacion),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MensajeCentrado(
    texto: String,
    esError: Boolean,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = texto,
            textAlign = TextAlign.Center,
            color = if (esError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onReintentar) {
            Text("Actualizar")
        }
    }
}
