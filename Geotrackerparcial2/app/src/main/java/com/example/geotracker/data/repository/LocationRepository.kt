package com.example.geotracker.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

// Obtiene la ubicación GPS actual con FusedLocationProviderClient
class LocationRepository(context: Context) {

    private val cliente = LocationServices.getFusedLocationProviderClient(context)

    // La pantalla pide el permiso antes de llamar a esta función.
    // Devuelve null si no se pudo obtener (por ejemplo, con el GPS apagado).
    @SuppressLint("MissingPermission")
    suspend fun obtenerUbicacionActual(): Location? {
        val actual = pedirUbicacionActual()
        return actual ?: ultimaUbicacionConocida()
    }

    @SuppressLint("MissingPermission")
    private suspend fun pedirUbicacionActual(): Location? =
        suspendCancellableCoroutine { continuacion ->
            val cancelacion = CancellationTokenSource()

            cliente.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancelacion.token)
                .addOnSuccessListener { continuacion.resume(it) }
                .addOnFailureListener { continuacion.resume(null) }

            continuacion.invokeOnCancellation { cancelacion.cancel() }
        }

    @SuppressLint("MissingPermission")
    private suspend fun ultimaUbicacionConocida(): Location? =
        suspendCancellableCoroutine { continuacion ->
            cliente.lastLocation
                .addOnSuccessListener { continuacion.resume(it) }
                .addOnFailureListener { continuacion.resume(null) }
        }
}
