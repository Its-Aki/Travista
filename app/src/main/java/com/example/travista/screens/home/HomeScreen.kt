package com.example.travista.screens.home

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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.travista.R
import com.example.travista.navigation.rootnavigation.BottomNavItems
import com.example.travista.navigation.ScreenNavigation
import com.example.travista.components.PlacesCarousel
import com.example.travista.components.TopPlacesCarousel
import com.example.travista.components.WeatherReportCard
import com.example.travista.viewmodel.topdestinations.TopDestinationsViewModel
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
import kotlinx.coroutines.delay


@Composable
fun HomeScreen(
    homeNavController: NavController,rootNavController: NavController
) {

    val viewModel: TopDestinationsViewModel = hiltViewModel()
    val scrollState = rememberLazyListState()
    val isScrolled = remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex > 0 || scrollState.firstVisibleItemScrollOffset > 395
        }
    }






    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = scrollState) {
            item {
                BackgroundWithSearch(
                    onSearchClick = {
                    },rootNavController = rootNavController
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                val uiState by viewModel.topDestinationsState.collectAsState()
                when (uiState) {
                    is TopDestinationsViewModel.TopDestinationsUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    is TopDestinationsViewModel.TopDestinationsUiState.Error -> {
                        val message = (uiState as TopDestinationsViewModel.TopDestinationsUiState.Error).message
                        Text("Error: $message", modifier = Modifier.padding(16.dp))
                    }

                    is TopDestinationsViewModel.TopDestinationsUiState.Success -> {
                        val data = (uiState as TopDestinationsViewModel.TopDestinationsUiState.Success).data

                        if (data.isNotEmpty()) {
                            TopPlacesCarousel(
                                placeName = data.map { it.name },
                                placeId = data.map { it.placeId },
                                photoUrlList = data.map { it.photoUrl },
                                placeAddress = data.map { it.address },
                                placeRating = List(data.size) { null },
                                placeTotalRatings = List(data.size) { null },
                                descriptionList = List(data.size) { null },
                                extraInfoList = List(data.size) { null },
                                onCardClick =  { placeId, name, address ->
                                    val name = name
                                    val address = address
                                    val placeId = placeId
                                    homeNavController.navigate(
                                        ScreenNavigation.Destination.passArgs(placeName = name, address = address.toString(), placeId = placeId)
                                    )
                                }
                            )
                        } else {
                            Text("No destinations available", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
            item {
                ExploreNearbySection(homeNavController)
            }
            item {
                WeatherReportCard(modifier = Modifier.padding(16.dp))
            }
            item {
                Spacer(Modifier.height(100.dp))
            }
//            items(15) {
//                Text(
//                    "Recommended Section $it",
//                    style = MaterialTheme.typography.bodyLarge,
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .fillMaxWidth()
//                )
//            }
        }

        AnimatedVisibility(
            visible = isScrolled.value,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -80 }, animationSpec = tween(300)),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -80 }, animationSpec = tween(300))
        ) {
            StickySearchBar(
                onSearchClick = {

                }, rootNavController = rootNavController
            )
        }
    }
}

@Composable
fun BackgroundWithSearch(onSearchClick: () -> Unit,rootNavController: NavController) {
    var imageIndex by remember { mutableIntStateOf(0) }
    val imageUrl = "https://picsum.photos/800/600?grayscale&random=$imageIndex"

    LaunchedEffect(Unit) {
        while (true) {
            delay(6000)
            imageIndex++
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .error(R.drawable.default_background_image1)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Discover New Places",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 50.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Plan your next adventure with us",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSearchClick,rootNavController = rootNavController
            )
        }
    }
}

@Composable
fun StickySearchBar(onSearchClick: () -> Unit,rootNavController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 1f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        SearchBar(modifier = Modifier.fillMaxWidth(), onClick = onSearchClick, rootNavController = rootNavController )
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    rootNavController: NavController
) {
    val isImagePresent = remember { mutableStateOf(true) }
    val backgroundColor = if (isImagePresent.value) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
    val borderColor = if (isImagePresent.value) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)

    Surface(
        shape = RoundedCornerShape(100.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = modifier
            .height(60.dp)
            .fillMaxWidth()
            .clickable(
                enabled = true,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick ={
                    rootNavController.navigate(BottomNavItems.Explore.route) {
                        popUpTo(rootNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = false
                    }

                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Search your destination...",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}



@Composable
fun ExploreNearbySection(homeNavController: NavController) {
    val context = LocalContext.current

    // State for location
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
                    }
                } else {
                    isLoadingLocation = false
                }
            }
        } else {
            Toast.makeText(context, "Location permission is required", Toast.LENGTH_LONG).show()
            isLoadingLocation = false
        }
    }

    // Location settings launcher - This will show the system dialog to enable GPS
    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // User enabled location settings
            getLocationAfterPermissions(fusedLocationClient, context) { location ->
                currentLocation = location
                isLoadingLocation = false
                if (location != null) {
                    Toast.makeText(context, "Location enabled successfully!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // User didn't enable location settings
            Toast.makeText(context, "Location services are required for this feature", Toast.LENGTH_LONG).show()
            isLoadingLocation = false
        }
    }

    fun checkAndRequestLocationPermission(onLocationReady: (Location?) -> Unit) {
        val currentPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        hasLocationPermission = currentPermission
        isLoadingLocation = true

        if (currentPermission) {
            // Permission already granted, check location settings
            checkLocationSettings(locationSettingsClient, context, locationSettingsLauncher) { settingsEnabled ->
                if (settingsEnabled) {
                    getLocationAfterPermissions(fusedLocationClient, context) { location ->
                        currentLocation = location
                        isLoadingLocation = false
                        onLocationReady(location)
                    }
                } else {
                    isLoadingLocation = false
                    onLocationReady(null)
                }
            }
        } else {
            // Request app permission first
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Categories remain the same
    val categories = listOf(
        "lodging" to Pair(Icons.Default.Hotel, "Hotels"),
        "restaurant" to Pair(Icons.Default.Restaurant, "Restaurants"),
        "cafe" to Pair(Icons.Default.LocalCafe, "Cafes"),
        "gas_station" to Pair(Icons.Default.LocalGasStation, "Gas Stations"),
        "atm" to Pair(Icons.Default.LocalAtm, "ATMs"),
        "bank" to Pair(Icons.Default.AccountBalance,"Banks"),
        "hospital" to Pair(Icons.Default.LocalHospital,"Hospitals"),
        "pharmacy" to Pair(Icons.Default.LocalPharmacy,"Pharmacy"),
        "supermarket" to Pair(Icons.Default.ShoppingBasket,"Supermarket"),
        "shopping_mall" to Pair(Icons.Default.ShoppingBag,"Shopping Mall"),
        "park" to Pair(Icons.Default.Park,"Parks"),
        "parking" to Pair(Icons.Default.LocalParking,"Parking"),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Explore Nearby",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Show loading indicator if getting location
        if (isLoadingLocation) {
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Setting up location...")
            }
        }

        // Grid layout
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.chunked(3).forEach { rowCategories ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowCategories.forEach { (key, iconDisplayPair) ->
                        CategoryCard(
                            key = key,
                            icon = iconDisplayPair.first,
                            displayText = iconDisplayPair.second,
                            onCardClick = {
                                checkAndRequestLocationPermission { location ->
                                    if (location != null) {
                                        homeNavController.navigate(
                                            ScreenNavigation.NearbyPlaces.passArgs(
                                                placeType = key
                                            )
                                        )
                                    } else {
                                        Toast.makeText(context, "Unable to get location", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.weight(0.5f)
                        )
                    }
                }
            }
        }
    }
}

// Function to check and request location settings (GPS enable dialog)
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
        .setAlwaysShow(true) // Shows dialog even if "Never ask again" was selected
        .build()

    val locationSettingsTask: Task<LocationSettingsResponse> =
        settingsClient.checkLocationSettings(locationSettingsRequest)

    locationSettingsTask.addOnCompleteListener { task ->
        try {
            // Location settings are enabled
            task.getResult(ApiException::class.java)
            onResult(true)
        } catch (exception: ApiException) {
            when (exception.statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    try {
                        // Location settings need to be resolved, show dialog
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
                    // Location settings cannot be changed
                    Toast.makeText(context, "Location settings cannot be changed", Toast.LENGTH_LONG).show()
                    onResult(false)
                }
                else -> {
                    onResult(false)
                }
            }
        }
    }
}

// Simplified location getting after all permissions are granted
@SuppressLint("MissingPermission")
fun getLocationAfterPermissions(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    onLocationReceived: (Location?) -> Unit
) {
    // First try last known location
    fusedLocationClient.lastLocation.addOnCompleteListener { task ->
        if (task.isSuccessful && task.result != null) {
            onLocationReceived(task.result)
        } else {
            // Request fresh location
            requestFreshLocation(fusedLocationClient, context, onLocationReceived)
        }
    }
}

// Keep your existing helper functions
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

    // Set timeout
    Handler(Looper.getMainLooper()).postDelayed({
        fusedLocationClient.removeLocationUpdates(locationCallback)
        onLocationReceived(null)
    }, 15000L)
}

@Composable
fun CategoryCard(
    key: String,
    icon: ImageVector,
    displayText: String,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = displayText,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = displayText,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
