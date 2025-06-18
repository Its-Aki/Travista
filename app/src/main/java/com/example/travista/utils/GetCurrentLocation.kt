package com.example.travista.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume


suspend fun getCurrentLocationString(
    context: Context,
    timeoutMs: Long = 15000L /// in milliseconds
): String?
{
    // Check permissions first
    val permissionFine = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val permissionCoarse = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    if (permissionFine != PackageManager.PERMISSION_GRANTED &&
        permissionCoarse != PackageManager.PERMISSION_GRANTED
    ) {
        Log.e("LocationUtil", "Location permissions not granted")
        return null
    }

    // Check if location services are enabled
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    if (!isGpsEnabled && !isNetworkEnabled) {
        Log.e("LocationUtil", "Location services are disabled")
        return null
    }

    return try {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // First try to get last known location
        val lastLocation = getLastKnownLocation(fusedLocationClient)
        if (lastLocation != null) {
            Log.d("LocationUtil", "Got last known location")
            return formatLocationString(lastLocation)
        }

        Log.d("LocationUtil", "Last location null, requesting fresh location")

        // If last location is null, request fresh location with timeout
        val freshLocation = withTimeoutOrNull(timeoutMs) {
            requestFreshLocation(fusedLocationClient)
        }

        if (freshLocation != null) {
            Log.d("LocationUtil", "Got fresh location")
            formatLocationString(freshLocation)
        } else {
            Log.e("LocationUtil", "Failed to get fresh location within timeout")
            null
        }

    } catch (e: Exception) {
        Log.e("LocationUtil", "Error getting location: ${e.localizedMessage}", e)
        null
    }
}

@SuppressLint("MissingPermission")
private suspend fun getLastKnownLocation(
    fusedLocationClient: FusedLocationProviderClient
): Location? {
    return try {
        fusedLocationClient.lastLocation.await()
    } catch (e: Exception) {
        Log.e("LocationUtil", "Error getting last location: ${e.localizedMessage}")
        null
    }
}

@SuppressLint("MissingPermission")
private suspend fun requestFreshLocation(
    fusedLocationClient: FusedLocationProviderClient
): Location? = suspendCancellableCoroutine { continuation ->

    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10000L
    ).apply {
        setMinUpdateIntervalMillis(5000L)
        setMaxUpdateDelayMillis(12000L)
    }.build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            fusedLocationClient.removeLocationUpdates(this)

            if (continuation.isActive) {
                continuation.resume(location)
            }
        }
    }

    // Set up cancellation
    continuation.invokeOnCancellation {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    try {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    } catch (e: Exception) {
        if (continuation.isActive) {
            continuation.resume(null)
        }
    }
}

private fun formatLocationString(location: Location): String {
    return "${location.latitude},${location.longitude}"
}