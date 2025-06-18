package com.example.travista.components

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URL
import java.text.DecimalFormat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task

// Data classes for Open-Meteo API response
@Serializable
data class WeatherResponse(
    val current: CurrentWeather
)

@Serializable
data class CurrentWeather(
    val temperature_2m: Double,
    val relative_humidity_2m: Int,
    val apparent_temperature: Double,
    val precipitation_probability: Int,
    val visibility: Double,
    val wind_speed_10m: Double
)

// Weather data for UI
data class WeatherData(
    val temperature: String,
    val feelsLike: String,
    val visibility: String,
    val precipitationProbability: String,
    val windSpeed: String
)

// UI State
sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val weather: WeatherData) : WeatherUiState()
    object LocationPermissionDenied : WeatherUiState()
    object LocationSettingsDisabled : WeatherUiState()
    object Error : WeatherUiState()
}

@Composable
fun WeatherReportCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var uiState by remember { mutableStateOf<WeatherUiState>(WeatherUiState.Loading) }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var isLoadingLocation by remember { mutableStateOf(false) }

    // State to track permission status
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Create FusedLocationProviderClient
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Location settings client
    val locationSettingsClient = remember {
        LocationServices.getSettingsClient(context)
    }

    // Permission launcher for app permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted) {
            // After app permission granted, check location settings
            checkLocationSettings(locationSettingsClient, context) { settingsEnabled ->
                if (settingsEnabled) {
                    getLocationAfterPermissions(fusedLocationClient, context) { location ->
                        currentLocation = location
                        isLoadingLocation = false
                        if (location != null) {
                            scope.launch {
                                fetchWeatherData(location) { weatherState ->
                                    uiState = weatherState
                                }
                            }
                        } else {
                            uiState = WeatherUiState.Error
                        }
                    }
                } else {
                    uiState = WeatherUiState.LocationSettingsDisabled
                    isLoadingLocation = false
                }
            }
        } else {
            uiState = WeatherUiState.LocationPermissionDenied
            isLoadingLocation = false
        }
    }

    // Location settings launcher
    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // User enabled location settings
            getLocationAfterPermissions(fusedLocationClient, context) { location ->
                currentLocation = location
                isLoadingLocation = false
                if (location != null) {
                    scope.launch {
                        fetchWeatherData(location) { weatherState ->
                            uiState = weatherState
                        }
                    }
                } else {
                    uiState = WeatherUiState.Error
                }
            }
        } else {
            uiState = WeatherUiState.LocationSettingsDisabled
            isLoadingLocation = false
        }
    }

    fun checkAndRequestLocationPermission() {
        val currentPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        hasLocationPermission = currentPermission
        isLoadingLocation = true
        uiState = WeatherUiState.Loading

        if (currentPermission) {
            // Permission already granted, check location settings
            checkLocationSettings(locationSettingsClient, context, locationSettingsLauncher) { settingsEnabled ->
                if (settingsEnabled) {
                    getLocationAfterPermissions(fusedLocationClient, context) { location ->
                        currentLocation = location
                        isLoadingLocation = false
                        if (location != null) {
                            scope.launch {
                                fetchWeatherData(location) { weatherState ->
                                    uiState = weatherState
                                }
                            }
                        } else {
                            uiState = WeatherUiState.Error
                        }
                    }
                } else {
                    uiState = WeatherUiState.LocationSettingsDisabled
                    isLoadingLocation = false
                }
            }
        } else {
            // Request app permission first
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Initialize on first composition
    LaunchedEffect(Unit) {
        checkAndRequestLocationPermission()
    }

    Column(modifier = modifier) {
        WeatherCard(
            uiState = uiState,
            isLoadingLocation = isLoadingLocation,
            onRefresh = {
                checkAndRequestLocationPermission()
            }
        )
    }
}

@Composable
private fun WeatherCard(
    uiState: WeatherUiState,
    isLoadingLocation: Boolean,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        when (uiState) {
            is WeatherUiState.Loading -> {
                LoadingContent(isLoadingLocation)
            }
            is WeatherUiState.Success -> {
                WeatherContent(
                    weather = uiState.weather,
                    onRefresh = onRefresh
                )
            }
            is WeatherUiState.LocationPermissionDenied -> {
                ErrorContent(
                    title = "Location Permission Required",
                    message = "Please allow location access to view weather information",
                    icon = Icons.Default.LocationOff,
                    onRefresh = onRefresh
                )
            }
            is WeatherUiState.LocationSettingsDisabled -> {
                ErrorContent(
                    title = "Location Services Disabled",
                    message = "Please enable location services to view weather information",
                    icon = Icons.Default.LocationOff,
                    onRefresh = onRefresh
                )
            }
            is WeatherUiState.Error -> {
                ErrorContent(
                    title = "Unable to Load Weather",
                    message = "Check your connection and try again",
                    icon = Icons.Default.Refresh,
                    onRefresh = onRefresh
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(isLoadingLocation: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isLoadingLocation) "Getting location..." else "Loading weather...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun WeatherContent(
    weather: WeatherData,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Current Weather",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Temperature
        Text(
            text = weather.temperature,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Weather details
        WeatherDetailRow("Feels like", weather.feelsLike)
        WeatherDetailRow("Visibility", weather.visibility)
        WeatherDetailRow("Precipitation", weather.precipitationProbability)
        WeatherDetailRow("Wind Speed", weather.windSpeed)
    }
}

@Composable
private fun ErrorContent(
    title: String,
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Try Again")
        }
    }
}

@Composable
private fun WeatherDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

// Location handling functions (same as in ExploreNearbySection)
fun checkLocationSettings(
    settingsClient: SettingsClient,
    context: Context,
    locationSettingsLauncher: ActivityResultLauncher<IntentSenderRequest>? = null,
    onResult: (Boolean) -> Unit
) {
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10000L
    ).build()

    val locationSettingsRequest = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
        .setAlwaysShow(true)
        .build()

    val locationSettingsTask: Task<LocationSettingsResponse> =
        settingsClient.checkLocationSettings(locationSettingsRequest)

    locationSettingsTask.addOnCompleteListener { task ->
        try {
            task.getResult(ApiException::class.java)
            onResult(true)
        } catch (exception: ApiException) {
            when (exception.statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    try {
                        val resolvable = exception as ResolvableApiException
                        val intentSenderRequest = IntentSenderRequest.Builder(resolvable.resolution).build()

                        if (locationSettingsLauncher != null) {
                            locationSettingsLauncher.launch(intentSenderRequest)
                        } else {
                            onResult(false)
                        }
                    } catch (sie: IntentSender.SendIntentException) {
                        Log.e("LocationSettings", "Unable to execute request.")
                        onResult(false)
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    onResult(false)
                }
                else -> {
                    onResult(false)
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
fun getLocationAfterPermissions(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    onLocationReceived: (Location?) -> Unit
) {
    fusedLocationClient.lastLocation.addOnCompleteListener { task ->
        if (task.isSuccessful && task.result != null) {
            onLocationReceived(task.result)
        } else {
            requestFreshLocation(fusedLocationClient, context, onLocationReceived)
        }
    }
}

@SuppressLint("MissingPermission")
fun requestFreshLocation(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    onLocationReceived: (Location?) -> Unit
) {
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10000L
    ).apply {
        setMinUpdateIntervalMillis(5000L)
        setMaxUpdateDelayMillis(15000L)
    }.build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            fusedLocationClient.removeLocationUpdates(this)
            onLocationReceived(location)
        }
    }

    fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )

    Handler(Looper.getMainLooper()).postDelayed({
        fusedLocationClient.removeLocationUpdates(locationCallback)
        onLocationReceived(null)
    }, 15000L)
}

// Suspend function to fetch weather data
private suspend fun fetchWeatherData(
    location: Location,
    onResult: (WeatherUiState) -> Unit
) {
    try {
        Log.d("WeatherComposable", "Starting to fetch weather data for location: ${location.latitude}, ${location.longitude}")

        val weatherData = withContext(Dispatchers.IO) {
            fetchWeatherFromApi(location.latitude.toString(), location.longitude.toString())
        }

        if (weatherData != null) {
            Log.d("WeatherComposable", "Weather data fetched successfully: $weatherData")
            onResult(WeatherUiState.Success(weatherData))
        } else {
            Log.e("WeatherComposable", "Weather data is null - API call failed")
            onResult(WeatherUiState.Error)
        }

    } catch (e: Exception) {
        Log.e("WeatherComposable", "Exception in fetchWeatherData: ${e.message}", e)
        onResult(WeatherUiState.Error)
    }
}

// Function to fetch weather from Open-Meteo API
private suspend fun fetchWeatherFromApi(lat: String, lon: String): WeatherData? {
    return try {
        val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation_probability,visibility,wind_speed_10m&timezone=auto"

        Log.d("WeatherComposable", "API URL: $url")

        val response = URL(url).readText()
        Log.d("WeatherComposable", "API Response: $response")

        val json = Json { ignoreUnknownKeys = true }
        val weatherResponse = json.decodeFromString<WeatherResponse>(response)

        Log.d("WeatherComposable", "Parsed weather response: $weatherResponse")

        val current = weatherResponse.current
        val df = DecimalFormat("#.#")

        val weatherData = WeatherData(
            temperature = "${df.format(current.temperature_2m)}°C",
            feelsLike = "${df.format(current.apparent_temperature)}°C",
            visibility = "${df.format(current.visibility / 1000)} km",
            precipitationProbability = "${current.precipitation_probability}%",
            windSpeed = "${df.format(current.wind_speed_10m)} km/h"
        )

        Log.d("WeatherComposable", "Final weather data: $weatherData")
        weatherData

    } catch (e: Exception) {
        Log.e("WeatherComposable", "Error fetching weather from API: ${e.message}", e)
        null
    }
}