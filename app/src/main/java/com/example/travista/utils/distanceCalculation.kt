package com.example.travista.utils

// Add this utility function to calculate distance between two points
import android.content.Context
import android.util.Log
import kotlin.math.*

/**
 * Calculate distance between two geographic points using Haversine formula
 * @param lat1 Current latitude
 * @param lon1 Current longitude
 * @param lat2 Destination latitude
 * @param lon2 Destination longitude
 * @return Distance in kilometers
 */
fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371.0 // Earth's radius in kilometers

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}

/**
 * Format distance for display
 * @param distanceKm Distance in kilometers
 * @return Formatted string (e.g., "1.2 km" or "850 m")
 */
fun formatDistance(distanceKm: Double): String {
    return when {
        distanceKm < 1.0 -> "${(distanceKm * 1000).roundToInt()} m"
        distanceKm < 10.0 -> String.format("%.1f km", distanceKm)
        else -> "${distanceKm.roundToInt()} km"
    }
}

// Data class for holding user location coordinates

data class UserLocation(val latitude: Double, val longitude: Double)

/**
 * Get current location coordinates using your existing getCurrentLocationString function
 */
suspend fun getCurrentLocationCoordinates(context: Context): UserLocation? {
    return try {
        val locationString = getCurrentLocationString(context)
        locationString?.let { parseLocationFromString(it) }
    } catch (e: Exception) {
        Log.e("DistanceUtil", "Error getting location coordinates: ${e.localizedMessage}")
        null
    }
}

/**
 * Parse location coordinates from your location string format "lat,lng"
 */
fun parseLocationFromString(locationString: String): UserLocation? {
    return try {
        val parts = locationString.split(",")
        if (parts.size >= 2) {
            val lat = parts[0].trim().toDoubleOrNull()
            val lng = parts[1].trim().toDoubleOrNull()
            if (lat != null && lng != null) {
                UserLocation(latitude = lat, longitude = lng)
            } else {
                Log.e("DistanceUtil", "Failed to parse coordinates from: $locationString")
                null
            }
        } else {
            Log.e("DistanceUtil", "Invalid location string format: $locationString")
            null
        }
    } catch (e: Exception) {
        Log.e("DistanceUtil", "Error parsing location string: ${e.localizedMessage}")
        null
    }
}