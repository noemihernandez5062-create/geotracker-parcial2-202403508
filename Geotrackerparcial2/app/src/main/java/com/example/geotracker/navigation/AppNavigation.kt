package com.example.geotracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.geotracker.data.local.TokenManager
import com.example.geotracker.ui.captura.CapturaScreen
import com.example.geotracker.ui.dashboard.DashboardScreen
import com.example.geotracker.ui.lista.ListaScreen
import com.example.geotracker.ui.login.LoginScreen
import com.example.geotracker.ui.mapa.MapaScreen
import com.example.geotracker.ui.registro.RegistroScreen

private const val CLAVE_CORREO_REGISTRADO = "correoRegistrado"

object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val DASHBOARD = "dashboard"
    const val CAPTURA = "captura"
    const val LISTA = "lista"
    const val MAPA = "mapa"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    // Si ya hay un token guardado se entra directo al Dashboard
    val inicio = if (TokenManager.haySesion()) Rutas.DASHBOARD else Rutas.LOGIN

    NavHost(navController = navController, startDestination = inicio) {

        composable(Rutas.LOGIN) { entrada ->
            // El registro deja aquí el correo de la cuenta recién creada
            val correoRegistrado by entrada.savedStateHandle
                .getStateFlow<String?>(CLAVE_CORREO_REGISTRADO, null)
                .collectAsStateWithLifecycle()

            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Rutas.DASHBOARD) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onCrearCuenta = { navController.navigate(Rutas.REGISTRO) },
                correoRegistrado = correoRegistrado,
                onCorreoRegistradoUsado = {
                    entrada.savedStateHandle[CLAVE_CORREO_REGISTRADO] = null
                }
            )
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onVolver = { navController.volver() },
                onRegistroExitoso = { correo ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(CLAVE_CORREO_REGISTRADO, correo)
                    navController.popBackStack()
                }
            )
        }

        composable(Rutas.DASHBOARD) {
            DashboardScreen(
                onCapturar = { navController.navigate(Rutas.CAPTURA) },
                onVerLista = { navController.navigate(Rutas.LISTA) },
                onVerMapa = { navController.navigate(Rutas.MAPA) },
                onCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.CAPTURA) {
            CapturaScreen(onVolver = { navController.volver() })
        }

        composable(Rutas.LISTA) {
            ListaScreen(
                onVolver = { navController.volver() },
                onVerMapa = { navController.navigate(Rutas.MAPA) }
            )
        }

        composable(Rutas.MAPA) {
            MapaScreen(onVolver = { navController.volver() })
        }
    }
}

// Regresa a la pantalla anterior. Si se toca "volver" dos veces muy rápido,
// la segunda vez se ignora para no dejar la navegación sin pantallas (en blanco).
private fun NavHostController.volver() {
    if (previousBackStackEntry != null) popBackStack()
}
