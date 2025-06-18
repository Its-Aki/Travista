package com.example.travista.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.net.URL

// Function to Check Real Internet Connectivity (Using HTTP Request)
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

//    it checks if the network claims to have internet
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
// Ensures the network really has internet access
            && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

// get current temperature from address
// using free apis,
// Both are completely free and require no API keys
//OpenStreetMap Nominatim API → Converts address → latitude/longitude.
//Open-Meteo API → Uses lat/lon to get current weather.



suspend fun getCurrentTemperature(address: String): Double? = withContext(Dispatchers.IO) {
    try {
        // 1. fetching  Geocode address using OpenStreetMap
        val encodedAddress = URLEncoder.encode(address, "UTF-8")
        val geoUrl = "https://nominatim.openstreetmap.org/search?q=$encodedAddress&format=json"
        val geoJson = URL(geoUrl).readText()
        val geoArray = JSONArray(geoJson)
        if (geoArray.length() == 0) return@withContext null

        val geoObject = geoArray.getJSONObject(0)
        val lat = geoObject.getString("lat")
        val lon = geoObject.getString("lon")

        // 2.  Fetching weather from Open-Meteo
        val weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true"
        val weatherJson = URL(weatherUrl).readText()
        val weatherObj = JSONObject(weatherJson)
        val currentWeather = weatherObj.getJSONObject("current_weather")
        return@withContext currentWeather.getDouble("temperature")
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext null
    }
}





    
