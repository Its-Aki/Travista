package com.example.travista.screens.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.travista.components.AllPlacesCard
import com.example.travista.components.LoadingLottieAnimation
import com.example.travista.data.nearbylocations.NearbyLocations
import com.example.travista.navigation.ScreenNavigation
import com.example.travista.utils.getCurrentLocationString
import com.example.travista.utils.isNetworkAvailable
import com.example.travista.viewmodel.NearbyLocationsViewModel
import kotlinx.coroutines.delay
import coil.ImageLoader
import androidx.compose.runtime.remember as remember1
import coil.request.CachePolicy
import com.example.travista.components.AllPlacesCarousel
import com.example.travista.utils.UserLocation
import com.example.travista.utils.calculateDistance
import com.example.travista.utils.formatDistance
import com.example.travista.utils.parseLocationFromString


@Composable
fun NearbyLocationsScreen(
    placeType: String,
    navController: NavController,
    viewModel: NearbyLocationsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val locationsResult by viewModel.nearbyLocationsData.collectAsState(
        initial = Result.success(
            emptyList()
        )
    )

    // Get user location from ViewModel instead of local state
    val userLocation by viewModel.userLocation.collectAsState()



    val hasNetwork = isNetworkAvailable(context)
    var showLoading by remember { mutableStateOf(true) }


    LaunchedEffect(placeType) {
        val isAlreadyLoaded = !locationsResult.getOrNull().isNullOrEmpty()

        if (!isAlreadyLoaded && hasNetwork) {
            showLoading = true // Set loading immediately

            val locationString = getCurrentLocationString(context)

            if (locationString != null) {
                Log.d("NearbyScreen", "Location obtained: $locationString")
                // Parse user location coordinates for distance calculation
                val parsedUserLocation = parseLocationFromString(locationString)

                val radius = 10000 // in metres

                viewModel.fetchNearbyLocationsData(locationString, placeType, radius,parsedUserLocation)
                delay(1300)
            } else {
                Log.e("NearbyScreen", "Failed to get location")
                Toast.makeText(
                    context,
                    "Unable to get your location. Please check location settings.",
                    Toast.LENGTH_LONG
                ).show()
            }

            showLoading = false

        } else if (!isAlreadyLoaded && !hasNetwork) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            showLoading = true
        } else {
            // Data already loaded, no API call
            showLoading = false
        }
    }

    if (showLoading) {
        LoadingLottieAnimation()
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            locationScreen(locationsResult, navController,userLocation)
        }
    }
}

@Composable
fun locationScreen(
    locationResult: Result<List<NearbyLocations>>,
    navController: NavController,
    userLocation: UserLocation? = null
) {
    val context = LocalContext.current

    val imageLoader: ImageLoader = remember1 {
        ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }

    when {
        locationResult.isSuccess -> {
            val places = locationResult.getOrNull().orEmpty()
            if (places.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Add spacing at top
                    item {
                        Spacer(modifier = Modifier.padding(4.dp))
                    }

                    // Add place cards using AllPlacesCard
                    items(places) { place ->
                        val distance = if (userLocation != null && place.geometry?.location != null)
                        {
                            val distanceKm = calculateDistance(
                                userLocation.latitude,
                                userLocation.longitude,
                                place.geometry.location.latitude ?: 0.0,
                                place.geometry.location.longitude ?: 0.0
                            )
                            formatDistance(distanceKm)
                        } else null


                        Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                            AllPlacesCard(
                                placeName = place.name ?: "",
                                photoUrl = place.getDestinationPhotoUrl()
                                    ?: "https://plus.unsplash.com/premium_photo-1725943391552-ac450c795074?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                                onClick = {
                                    navController.navigate(
                                        ScreenNavigation.DestinationFullDetails.passArgs(
                                            placeId = place.placeId ?: ""
                                        )
                                    )
                                },
                               // address = place.vicinity,
                                rating = place.rating?.toString(),
                                totalRatings = place.totalRatings?.toString(),
                                description = distance?.let { "📍$it from you" },
                                imageLoader = imageLoader
                            )
                        }
                    }

                    // Bottom spacing
                    item {
                        Box(modifier = Modifier.padding(29.dp))
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Loading nearby places....")
                }
            }
        }

        locationResult.isFailure -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No locations Found!!")
            }
        }

        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun ShowLocationCarousel(result: Result<List<NearbyLocations>>, navController: NavController) {
    when {
        result.isSuccess -> {
            val places = result.getOrNull().orEmpty()
            if (places.isNotEmpty()) {
                AllPlacesCarousel(
                    placeName = places.map { it.name },
                    placeId = places.map { it.placeId },
                    photoUrlList = places.map { it.getDestinationPhotoUrl() ?: "https://plus.unsplash.com/premium_photo-1725943391552-ac450c795074?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" },
                    placeRating = places.map { it.rating },
                    placeTotalRatings = places.map { it.totalRatings },
                    onCardClick = { placeId,_,_ ->
                        navController.navigate(ScreenNavigation.DestinationFullDetails.passArgs(placeId = placeId))
                    }
                )
            } else {
                Text(text="Loading nearby places....")
            }
        }
        result.isFailure -> {
            Text(text="No locations Found!!")
        }
        else -> {
            CircularProgressIndicator()
        }
    }
}