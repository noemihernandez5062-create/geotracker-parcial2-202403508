package com.example.geotracker.ui.registro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.geotracker.ui.MayusculaInicial
import com.example.geotracker.ui.theme.BotonDegradado
import com.example.geotracker.ui.theme.coloresBarra
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onVolver: () -> Unit,
    onRegistroExitoso: (correo: String) -> Unit,
    viewModel: RegistroViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.registroExitoso) {
        if (state.registroExitoso) onRegistroExitoso(state.correo.trim())
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                colors = coloresBarra(),
                title = { Text("Crear cuenta") },
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Campo(state.nombre, viewModel::onNombreChange, "Nombre", Icons.Filled.Person, !state.cargando)

            Campo(
                state.correo, viewModel::onCorreoChange, "Correo", Icons.Filled.Email, !state.cargando,
                tipoTeclado = KeyboardType.Email
            )

            Campo(
                state.telefono, viewModel::onTelefonoChange, "Teléfono (opcional)", Icons.Filled.Phone,
                !state.cargando, tipoTeclado = KeyboardType.Phone
            )

            Campo(
                state.password, viewModel::onPasswordChange, "Contraseña (mínimo 5)", Icons.Filled.Lock,
                !state.cargando, tipoTeclado = KeyboardType.Password, esPassword = true
            )

            Campo(
                state.confirmarPassword, viewModel::onConfirmarPasswordChange, "Confirmar contraseña",
                Icons.Filled.Lock, !state.cargando, tipoTeclado = KeyboardType.Password, esPassword = true
            )

            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            BotonDegradado(
                texto = "Registrarme",
                onClick = viewModel::registrar,
                cargando = state.cargando
            )
        }
    }
}

@Composable
private fun Campo(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    habilitado: Boolean,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    esPassword: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = null) },
        singleLine = true,
        enabled = habilitado,
        keyboardOptions = KeyboardOptions(
            keyboardType = tipoTeclado,
            // Mayúscula inicial solo en campos de texto normal (no en correo ni contraseña)
            capitalization = if (tipoTeclado == KeyboardType.Text) {
                KeyboardCapitalization.Sentences
            } else {
                KeyboardCapitalization.None
            }
        ),
        visualTransformation = when {
            esPassword -> PasswordVisualTransformation()
            tipoTeclado == KeyboardType.Text -> MayusculaInicial
            else -> VisualTransformation.None
        },
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    )
}
