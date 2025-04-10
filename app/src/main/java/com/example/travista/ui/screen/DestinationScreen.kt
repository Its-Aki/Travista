package com.example.travista.ui.screen

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.travista.data.detailsofdestinaton.DetailsOFDestination
import com.example.travista.data.detinationfulldetails.DestinationFullDetails
import com.example.travista.data.getApikey
import com.example.travista.ui.navigation.tabnavigation.ScreenNavigation
import com.example.travista.ui.viewmodel.DestinationFullDetailsViewModel
import com.example.travista.ui.viewmodel.DestinationViewModel
import com.example.travista.utils.isNetworkAvailable
import kotlinx.coroutines.delay


@Composable
fun DestinationDescription(
    placeName: String,
    placeAddress: String,
    placeId: String,
    navController: NavController,
    viewModel: DestinationViewModel = hiltViewModel(),
    photoViewModel: DestinationFullDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val hotelResult by viewModel.topHotelData.collectAsState(initial = Result.success(emptyList()))
    val restaurantResult by viewModel.topRestaurantData.collectAsState(initial = Result.success(emptyList()))
    val attractionResult by viewModel.topAttractionsData.collectAsState(initial = Result.success(emptyList()))
    val photoResult by photoViewModel.destinationFullDetails.collectAsState()

    val hasNetwork = isNetworkAvailable(context)

    var showLoading by remember { mutableStateOf(true) }

    LaunchedEffect(placeName, placeId, placeAddress) {
        val isAlreadyLoaded =
            !hotelResult.getOrNull().isNullOrEmpty() &&
                    !restaurantResult.getOrNull().isNullOrEmpty() &&
                    !attractionResult.getOrNull().isNullOrEmpty() &&
                    !(photoResult?.getOrNull()?.photos.isNullOrEmpty())

        if (!isAlreadyLoaded && hasNetwork) {
            viewModel.fetchDestinationData(placeName, placeAddress)
            photoViewModel.fetchDestinationFullDetails(placeId, getApikey())
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
            TopBar(placeName, navController)
            ContentScreen(
                hotelResult, restaurantResult, attractionResult, photoResult,
                navController
            )
        }
    }
}

// top bar composable with title, back button and favorite icon
@Composable
fun TopBar(placeName:String,navController: NavController) {
    var isFavorited by rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Handle back action */
        navController.popBackStack()}) {
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
        IconButton(
            onClick = { isFavorited = !isFavorited }
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = if (isFavorited) Color.Red else Color.White, // Conditional background color
                        shape = MaterialTheme.shapes.small // Rounded corners (optional)
                    )
                    .padding(8.dp) // Padding around the icon
            ) {
                Icon(
                    imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, // Switch icon
                    contentDescription = "Favorite",
                    tint =  if (isFavorited) Color.White else Color.Black // Icon color (white on colored background)
                )
            }
        }
        //
    }
}

@Composable
fun ContentScreen(
    hotelResult: Result<List<DetailsOFDestination>>,
    restaurantResult: Result<List<DetailsOFDestination>>,
    attractionResult: Result<List<DetailsOFDestination>>,
    photoResult: Result<DestinationFullDetails>?,
    navController: NavController
) {
    val scrollState = rememberScrollState()


    Column(
        modifier = Modifier.verticalScroll(scrollState),

    ) {
     showPhoto(photoResult)

        Column(Modifier.padding(start = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ExpandableText("highdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsddhighdfsdd ")

            Spacer(modifier = Modifier.padding(4.dp))

            Text(
                text = "Hotels",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text("Handpicked stays for an unforgettable experience!")
            ShowPlacesCarousel(hotelResult,navController)

            Spacer(modifier = Modifier.padding(4.dp))

            Text(
                text = "Restaurants",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text("Discover your next favorite eatery!")
            ShowPlacesCarousel(restaurantResult,navController)

            Spacer(modifier = Modifier.padding(4.dp))

            Text(
                text = "Things to do",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text("Discover must-do activities in the area!")
            ShowPlacesCarousel(attractionResult, navController)

            Spacer(modifier = Modifier.padding(40.dp))

        }
    }
}


@Composable
fun ShowPlacesCarousel(result: Result<List<DetailsOFDestination>>,navController: NavController) {
    when {
        result.isSuccess -> {
            val places = result.getOrNull().orEmpty()
            if (places.isNotEmpty()) {
                PlacesCarousel(
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
                Text(text="No hotels found!!")
            }
        }
        result.isFailure -> {
         Text(text="No hotels found!!")
        }
        else -> {
            CircularProgressIndicator()
        }
    }
}


// composable for showing summary of the destination with read more functionality
@Composable
private fun ExpandableText(summary:String) {
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val isExpandable by remember { derivedStateOf { textLayoutResult?.didOverflowHeight ?: false } }
    var isExpanded by remember { mutableStateOf(false) }
    val isButtonShown by remember { derivedStateOf { isExpandable || isExpanded } }
    val interactionSource = remember { MutableInteractionSource() } // for removing ripple effect

    Column{


        Text(
            text = summary,
            modifier = Modifier.animateContentSize().clickable(
                interactionSource = interactionSource,
                indication = null

            ){ isExpanded = !isExpanded },
            maxLines = if (isExpanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult = it },

            )


        if (isButtonShown) {
            Text(
                text = if (isExpanded) "" else "Read more",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { isExpanded = !isExpanded }
            )

        }
    }
}
// composable to display images in the imagecarousel after fetching photoreference
@Composable
fun showPhoto(result: Result<DestinationFullDetails>?)
{
    when {
        false -> {
            CircularProgressIndicator()
        }


        result?.isFailure == true -> {
            Text("Error: ${result.exceptionOrNull()?.message}")
        }

        result?.isSuccess == true -> {
            val destinationFullDetails = result.getOrNull()
            destinationFullDetails?.let {
                val defaultImageUrls = listOf(
                    "https://plus.unsplash.com/premium_vector-1739976218147-36f2581a0bbd?q=80&w=2148&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
                )

                // Fallback to default images if no photos are available
                val photoUrls = it.photos?.mapNotNull { photo ->
                    photo.photoReference?.let { reference ->
                        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$reference&key=${getApikey()}"
                    }
                } ?: emptyList()

                // Image Carousel for displaying images of restaurant if available
                if (photoUrls.isEmpty()) {
                    ImageCarousel(imageUrls = defaultImageUrls)
                } else {
                    ImageCarousel(imageUrls = photoUrls)
                }


            }
        }
    }
}
