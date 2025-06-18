package com.example.travista.screens.explore

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.travista.components.AllPlacesCard
import com.example.travista.components.LoadingLottieAnimation
import com.example.travista.data.detailsofdestinaton.DetailsOFDestination
import com.example.travista.navigation.ScreenNavigation
import com.example.travista.utils.isNetworkAvailable
import com.example.travista.viewmodel.DestinationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import coil.ImageLoader
import androidx.compose.runtime.remember as remember1
import coil.request.CachePolicy


@Composable
fun AllPlacesScreen(
    navController: NavController,
    placeName: String,
    placeAddress: String,
    type:String,
    viewModel: DestinationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val hotelResult by viewModel.topHotelData.collectAsState(initial = Result.success(emptyList()))
    val restaurantResult by viewModel.topRestaurantData.collectAsState(
        initial = Result.success(
            emptyList()
        )
    )
    val attractionResult by viewModel.topAttractionsData.collectAsState(
        initial = Result.success(
            emptyList()
        )
    )

    val hasNetwork = isNetworkAvailable(context)
    var showLoading by remember { mutableStateOf(true) }

    LaunchedEffect(placeName, placeAddress)
    {
        val isAlreadyLoaded =
            !hotelResult.getOrNull().isNullOrEmpty() ||
                    !restaurantResult.getOrNull().isNullOrEmpty() ||
                    !attractionResult.getOrNull().isNullOrEmpty()

        if (!isAlreadyLoaded && hasNetwork) {
            viewModel.fetchDestinationData(placeName, placeAddress)
            delay(1300)
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
            AllPlacesTopBar(placeName, navController)
            ContentScreen(
                hotelResult, restaurantResult, attractionResult,
                navController, viewModel = viewModel,
                placeName = placeName,
                placeAddress = placeAddress,type=type
            )
        }
    }
}

// content - Modified to use LazyColumn
@Composable
fun ContentScreen(
    hotelResult: Result<List<DetailsOFDestination>>,
    restaurantResult: Result<List<DetailsOFDestination>>,
    attractionResult: Result<List<DetailsOFDestination>>,
    navController: NavController,
    viewModel: DestinationViewModel,
    placeName: String,
    placeAddress: String,
    type: String
) {
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val imageLoader: ImageLoader = remember1 {
        ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }

    // Get the current data based on type
    val currentResult = when (type) {
        "hotels" -> hotelResult
        "restaurants" -> restaurantResult
        "attractions" -> attractionResult
        else -> Result.success(emptyList())
    }

    when {
        currentResult.isSuccess -> {
            val places = currentResult.getOrNull().orEmpty()
            if (places.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Add spacing at top
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Add place cards
                    items(places) { place ->
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
                                rating = place.rating?.toString(),
                                totalRatings = place.totalRatings?.toString(),
                                imageLoader = imageLoader
                            )
                        }
                    }

                    // Load More button
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Load More",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.clickable(
                                        enabled = !isLoading,
                                        onClick = {
                                            isLoading = true
                                            when (type) {
                                                "hotels" -> viewModel.loadMoreHotels(placeName, placeAddress)
                                                "restaurants" -> viewModel.loadMoreRestaurants(placeName, placeAddress)
                                                "attractions" -> viewModel.loadMoreAttractions(placeName, placeAddress)
                                            }
                                            CoroutineScope(Dispatchers.Main).launch {
                                                delay(4000)
                                                isLoading = false
                                            }
                                        }
                                    )
                                )
                            }
                        }
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No locations found!!")
                }
            }
        }

        currentResult.isFailure -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No locations found!!")
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

// top bar composable with title, back button and favorite icon
@Composable
fun AllPlacesTopBar(placeName: String, navController: NavController) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            navController.popBackStack()
        }) {
            Icon(
                imageVector = Icons.Outlined.ArrowBackIosNew,
                contentDescription = "Back"
            )
        }
        Text(
            text = placeName,
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}