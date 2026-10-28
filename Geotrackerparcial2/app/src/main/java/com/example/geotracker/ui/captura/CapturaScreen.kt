package com.example.geotracker.ui.captura

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.geotracker.ui.theme.BotonDegradado
import com.example.geotracker.ui.theme.IconoDegradado
import com.example.geotracker.ui.theme.coloresBarra
import com.example.geotracker.ui.theme.coloresTarjeta
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.geotracker.ui.MayusculaInicial
import com.example.geotracker.ui.formatearCoordenadas

private val permisosUbicacion = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

private fun tienePermisoUbicacion(context: Context): Boolean =
    permisosUbicacion.any {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CapturaScreen(
    onVolver: () -> Unit,
    viewModel: CapturaViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Solicitud del permiso en tiempo de ejecución
    val pedirPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.any { it }) {
            viewModel.capturarYGuardar()
        } else {
            viewModel.onPermisoDenegado()
        }
    }

    val onCapturar = {
        if (viewModel.formularioValido()) {
            if (tienePermisoUbicacion(context)) {
                viewModel.capturarYGuardar()
            } else {
                pedirPermiso.launch(permisosUbicacion)
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                colors = coloresBarra(),
                title = { Text("Capturar ubicación") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = coloresTarjeta(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        IconoDegradado(icono = Icons.Filled.LocationOn, tamano = 44.dp)
                        Text(
                            text = "Escribe un nombre y una descripción. Al presionar el botón se toma tu ubicación GPS actual y se guarda.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedTextField(
                        value = state.nombre,
                        onValueChange = viewModel::onNombreChange,
                        label = { Text("Nombre") },
                        singleLine = true,
                        enabled = !state.cargando,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        ),
                        visualTransformation = MayusculaInicial,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = state.descripcion,
                        onValueChange = viewModel::onDescripcionChange,
                        label = { Text("Descripción") },
                        minLines = 3,
                        enabled = !state.cargando,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        visualTransformation = MayusculaInicial,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    BotonDegradado(
                        texto = "Capturar y guardar ubicación",
                        icono = Icons.Filled.LocationOn,
                        onClick = onCapturar,
                        habilitado = !state.cargando
                    )
                }
            }

            // INDICADOR DE CARGA: captura GPS y envío al backend
            if (state.cargando) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                    Text(
                        text = when (state.paso) {
                            PasoCaptura.OBTENIENDO_GPS -> "Obteniendo ubicación GPS..."
                            else -> "Guardando en el servidor..."
                        }
                    )
                }
            }

            // MENSAJE DE ÉXITO O ERROR
            state.mensaje?.let { mensaje ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.esError) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (state.esError) Icons.Filled.Warning else Icons.Filled.CheckCircle,
                            contentDescription = null
                        )
                        Column {
                            Text(mensaje, style = MaterialTheme.typography.bodyLarge)

                            state.ubicacionGuardada?.takeIf { !state.esError }?.let { guardada ->
                                Text(
                                    text = "${guardada.nombre}: ${formatearCoordenadas(guardada.latitud, guardada.longitud)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
