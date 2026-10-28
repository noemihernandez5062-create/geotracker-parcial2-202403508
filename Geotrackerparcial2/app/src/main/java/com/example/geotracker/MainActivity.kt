package com.example.geotracker

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.geotracker.navigation.AppNavigation
import com.example.geotracker.ui.theme.FondoDegradado
import com.example.geotracker.ui.theme.GeoTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fondo oscuro: íconos de la barra de estado siempre claros
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        setContent {
            GeoTrackerTheme {
                // El degradado azul queda detrás de todas las pantallas
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(FondoDegradado)
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
