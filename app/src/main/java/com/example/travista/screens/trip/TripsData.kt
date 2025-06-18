package com.example.travista.screens.trip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.ModeOfTravel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.travista.R


@Composable
fun TripsData() {
    // The Card with elevation, shape, and padding
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),  // Padding around the Card
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.elevatedCardElevation(8.dp)  // Shadow or elevation for the card
    ) {
        // Inside the Card, use a Column to hold the content
        Column(
            modifier = Modifier
                .padding(16.dp) // Padding inside the Card
        ) {
            // Row for displaying the title and the arrow icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween // Align text and icon
            ) {
                Text(
                    text = "Take a flight from Goa to dff dff Dfsdgfhgjhjdgfhg sdfgh sdgfhgjh sdgfhgjh sdgfhg elhi dfjsfjsfjhksjhkjshjhsghshgjhsgjhsfjgsg jgsjgjgjgjlsjfs ",
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f) ,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = Bold
                )
                Box(Modifier.clip(CircleShape).clickable(onClick = {})) {
                    Icon(
                        imageVector = Icons.Default.KeyboardDoubleArrowRight,
                        contentDescription = "See details",
                        tint = Color.Blue
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))  // Spacer for separation

            // 2nd Row for displaying mode, distance and duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Row(Modifier.weight(1f)) {
                           Icon(
                               Icons.Default.ModeOfTravel,
                               contentDescription = "Mode of transport ",
                               tint = Color.Blue,
                               modifier = Modifier.size(16.dp)
                           )
                           Column {
                               Text(
                                   text = "Mode: Flight ",
                                   style = MaterialTheme.typography.bodySmall,
                                   maxLines = 1,
                                   overflow = TextOverflow.Ellipsis,
                                   modifier = Modifier.padding(start = 2.dp),
                                   fontWeight = Bold
                               )

                           }
                       }




                Row(modifier = Modifier.weight(1f).padding(start = 2.dp)){
                    Icon(painter= painterResource(R.drawable.baseline_route_24), contentDescription = "distance", tint = Color.Blue, modifier = Modifier.size(16.dp))
                    Text(text = "~500-800 km", style = MaterialTheme.typography.bodySmall, maxLines = 1,
                        overflow = TextOverflow.Ellipsis, fontWeight = Bold)


                }
                Row(modifier = Modifier.weight(1f).padding(start = 2.dp)){
                    Icon(Icons.Default.AccessTime, contentDescription = "time", tint = Color.Blue, modifier = Modifier.size(16.dp))
                    Text(text = "~1hour 20 m ", style = MaterialTheme.typography.bodySmall, maxLines = 1,
                        overflow = TextOverflow.Ellipsis, fontWeight = Bold)


                }

            }


            Spacer(modifier = Modifier.height(16.dp))  // Spacer for separation

            // "Buy" button at the bottom of the card
            HorizontalDivider()

        }
    }
}

@Preview
@Composable
fun TripsDataPreview() {
    TripsData()
}


//////--------------------------------------
//
//package com.example.travista.screens.trip
//import android.R.attr.fontWeight
//import android.widget.Toast
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AccessTime
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
//import androidx.compose.material.icons.filled.ModeOfTravel
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.ModalBottomSheet
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.material3.rememberModalBottomSheetState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.font.FontWeight.Companion.Bold
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.example.travista.R
//import com.example.travista.components.LoadingLottieAnimation
//import com.example.travista.components.PlacesCarousel
//import com.example.travista.data.detailsofdestinaton.DetailsOFDestination
//import com.example.travista.data.tripsteps.TravelStep
//import com.example.travista.navigation.ScreenNavigation
//import com.example.travista.viewmodel.TravelStepsViewModel
//import com.example.travista.utils.isNetworkAvailable
//import com.example.travista.viewmodel.DestinationViewModel
//import kotlinx.coroutines.delay
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TravelStepsScreen(
//    startingAddress: String,
//    destinationAddress: String,
//    navController: NavController,
//    viewModel: TravelStepsViewModel = hiltViewModel(),
//    nearbyPlaces: DestinationViewModel = hiltViewModel()
//) {
//    val context = LocalContext.current
//    val travelData by viewModel.travelDetails.collectAsState()
//    val hotelResult by nearbyPlaces.topHotelData.collectAsState(initial = Result.success(emptyList()))
//    val restaurantResult by nearbyPlaces.topRestaurantData.collectAsState(initial = Result.success(emptyList()))
//    val attractionResult by nearbyPlaces.topAttractionsData.collectAsState(initial = Result.success(emptyList()))
//
//    var showLoading = travelData == null && isNetworkAvailable(context)
//    val scrollState = rememberScrollState()
//
//    var selectedStep by remember { mutableStateOf<TravelStep?>(null) }
//    var showBottomSheet by remember { mutableStateOf(false) }
//    val bottomSheetState = rememberModalBottomSheetState()
//
//    LaunchedEffect(startingAddress, destinationAddress)
//    {
//        val isAlreadyLoaded =
//            !hotelResult.getOrNull().isNullOrEmpty() &&
//                    !restaurantResult.getOrNull().isNullOrEmpty() &&
//                    !attractionResult.getOrNull().isNullOrEmpty()
//
//        val placeName = destinationAddress.substringBefore(",").trim()
//        if (!isAlreadyLoaded && travelData == null && isNetworkAvailable(context)) {
//            viewModel.fetchTravelDetails(startingAddress,destinationAddress)
//            nearbyPlaces.fetchDestinationData(placeName, destinationAddress)
//            delay(1000)
//            showLoading = false
//        } else if (!isNetworkAvailable(context) && travelData == null) {
//            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
//            showLoading = true
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            // .padding(16.dp)
//            .verticalScroll(scrollState)
//    ) {
//        when {
//            showLoading -> {
//                LoadingLottieAnimation()
//            }
//
//            travelData == null -> {
//                Text("Loading...")
//            }
//
//            travelData!!.isFailure -> {
//                Text("Error: ${travelData!!.exceptionOrNull()?.message}")
//            }
//
//            travelData!!.isSuccess -> {
//                val steps = travelData!!.getOrNull()?.route ?: emptyList()
//                if (steps.isEmpty()) {
//                    Text("No travel steps found.")
//                } else {
//                    Text("Steps for travelling from $startingAddress to $destinationAddress")
//                    Spacer(Modifier.height(50.dp))
//                    steps.forEach { step ->
//                        TravelStepCard(
//                            step = step,
//                            onCardClick = {
//                                selectedStep = step
//                                showBottomSheet = true
//                            }
//                        )
//                        Spacer(modifier = Modifier.height(12.dp))
//                    }
//                    val destinationName = destinationAddress.substringBefore(",").trim()
//                    Text("Destination - $destinationName \n Cards ended ---- \n adding other details ")
//                    Text("Explore  $destinationName \n",fontWeight = Bold)
//                    Spacer(modifier = Modifier.height(20.dp))
//                    Text("Hotels",fontWeight = Bold)
//                    Spacer(modifier = Modifier.height(20.dp))
//                    DestinationPlacesCarousel(hotelResult,navController)
//                    Spacer(modifier = Modifier.height(20.dp))
//                    Text("Restaurants",fontWeight = Bold)
//                    Spacer(modifier = Modifier.height(20.dp))
//                    DestinationPlacesCarousel(restaurantResult,navController)
//                    Spacer(modifier = Modifier.height(20.dp))
//                    Text("Attractions",fontWeight = Bold)
//                    Spacer(modifier = Modifier.height(20.dp))
//                    DestinationPlacesCarousel(attractionResult,navController)
//
//                    Spacer(modifier = Modifier.height(150.dp))
//                }
//            }
//        }
//    }
//
//
//    // Modal Bottom Sheet for detailed view
//    if (showBottomSheet && selectedStep != null) {
//        ModalBottomSheet(
//            onDismissRequest = { showBottomSheet = false },
//            sheetState = bottomSheetState
//        ) {
//            TravelStepDetailSheet(
//                step = selectedStep!!,
//                onClose = { showBottomSheet = false }
//            )
//        }
//    }
//
//}
//
//@Composable
//fun TravelStepCard(
//    step: TravelStep,
//    onCardClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(Color.White),
//        elevation = CardDefaults.elevatedCardElevation(8.dp)
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            // Header Row with step title and arrow
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column(modifier = Modifier.weight(1f)) {
//                    Surface(
//                        color = Color(0xFF1976D2).copy(alpha = 0.1f),
//                        shape = RoundedCornerShape(12.dp),
//                        modifier = Modifier.padding(bottom = 8.dp)
//                    ) {
//                        Text(
//                            text = "STEP ${step.stepIndex}",
//                            style = MaterialTheme.typography.labelSmall.copy(
//                                fontSize = 11.sp,
//                                letterSpacing = 0.8.sp
//                            ),
//                            color = Color(0xFF1976D2),
//                            fontWeight = FontWeight.SemiBold,
//                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//                        )
//                    }
//                    Text(
//                        text = step.title,
//                        maxLines = 2,
//                        overflow = TextOverflow.Ellipsis,
//                        style = MaterialTheme.typography.bodyLarge,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier.padding(top = 2.dp)
//                    )
//                }
//
//                Box(
//                    modifier = Modifier
//                        .clip(CircleShape)
//                        .clickable(onClick = onCardClick)
//                        .padding(8.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.KeyboardDoubleArrowRight,
//                        contentDescription = "See details",
//                        tint = Color.Blue,
//                        modifier = Modifier.size(20.dp)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Details Row with mode, distance, and duration
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                // Travel Mode
//                Row(
//                    modifier = Modifier.weight(1f),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        Icons.Default.ModeOfTravel,
//                        contentDescription = "Mode of transport",
//                        tint = Color.Blue,
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Text(
//                        text = step.travelMode,
//                        style = MaterialTheme.typography.bodySmall,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                        modifier = Modifier.padding(start = 4.dp),
//                        fontWeight = FontWeight.Medium
//                    )
//                }
//
//                // Distance
//                Row(
//                    modifier = Modifier
//                        .weight(1f)
//                        .padding(horizontal = 4.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.baseline_route_24),
//                        contentDescription = "distance",
//                        tint = Color.Blue,
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Text(
//                        text = step.distance,
//                        style = MaterialTheme.typography.bodySmall,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                        modifier = Modifier.padding(start = 4.dp),
//                        fontWeight = FontWeight.Medium
//                    )
//                }
//
//                // Duration
//                Row(
//                    modifier = Modifier.weight(1f),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        Icons.Default.AccessTime,
//                        contentDescription = "time",
//                        tint = Color.Blue,
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Text(
//                        text = step.duration,
//                        style = MaterialTheme.typography.bodySmall,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                        modifier = Modifier.padding(start = 4.dp),
//                        fontWeight = FontWeight.Medium
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Destination
//            if (step.to.isNotEmpty()) {
//                Text(
//                    text = "To: ${step.to}",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = Color.Gray,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//            }
//
//            HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
//        }
//    }
//}
//
//@Composable
//fun TravelStepDetailSheet(
//    step: TravelStep,
//    onClose: () -> Unit
//) {
//    val scrollState = rememberScrollState()
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//            .verticalScroll(scrollState)
//    ) {
//        // Header with close button
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = "Step ${step.stepIndex} Details",
//                style = MaterialTheme.typography.headlineSmall,
//                fontWeight = FontWeight.Bold
//            )
//
//            IconButton(onClick = onClose) {
//                Icon(
//                    Icons.Default.Close,
//                    contentDescription = "Close",
//                    tint = Color.Gray
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Title
//        Card(
//            modifier = Modifier.fillMaxWidth(),
//            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
//        ) {
//            Text(
//                text = step.title,
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(16.dp)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Travel Details
//        Card(
//            modifier = Modifier.fillMaxWidth(),
//            colors = CardDefaults.cardColors(Color.White),
//            elevation = CardDefaults.elevatedCardElevation(4.dp)
//        ) {
//            Column(modifier = Modifier.padding(16.dp)) {
//                Text(
//                    text = "Travel Information",
//                    style = MaterialTheme.typography.titleSmall,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.Blue
//                )
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                DetailRow(label = "Mode", value = step.travelMode)
//                DetailRow(label = "Distance", value = step.distance)
//                DetailRow(label = "Duration", value = step.duration)
//                DetailRow(label = "Destination", value = step.to)
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Summary
//        if (step.summary.isNotEmpty()) {
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                colors = CardDefaults.cardColors(Color.White),
//                elevation = CardDefaults.elevatedCardElevation(4.dp)
//            ) {
//                Column(modifier = Modifier.padding(16.dp)) {
//                    Text(
//                        text = "Summary",
//                        style = MaterialTheme.typography.titleSmall,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.Blue
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Text(
//                        text = step.summary,
//                        style = MaterialTheme.typography.bodyMedium,
//                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//
//        // Guides
//        if (step.guides.isNotEmpty()) {
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                colors = CardDefaults.cardColors(Color.White),
//                elevation = CardDefaults.elevatedCardElevation(4.dp)
//            ) {
//                Column(modifier = Modifier.padding(16.dp)) {
//                    Text(
//                        text = "Travel Guide",
//                        style = MaterialTheme.typography.titleSmall,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.Blue
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Text(
//                        text = step.guides,
//                        style = MaterialTheme.typography.bodyMedium,
//                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
//                    )
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(32.dp))
//    }
//}
//
//@Composable
//fun DetailRow(label: String, value: String) {
//    if (value.isNotEmpty()) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = "$label:",
//                style = MaterialTheme.typography.bodyMedium,
//                fontWeight = FontWeight.Medium,
//                color = Color.Gray,
//                modifier = Modifier.weight(0.3f)
//            )
//            Text(
//                text = value,
//                style = MaterialTheme.typography.bodyMedium,
//                modifier = Modifier.weight(0.7f)
//            )
//        }
//        Spacer(modifier = Modifier.height(8.dp))
//    }
//}
//
//
////// places carousel for displaying hotelResult,restaurantResult and attractionResult
//// carousel start ----
//@Composable
//fun DestinationPlacesCarousel(result: Result<List<DetailsOFDestination>>,navController: NavController) {
//    when {
//        result.isSuccess -> {
//            val places = result.getOrNull().orEmpty()
//            if (places.isNotEmpty()) {
//                PlacesCarousel(
//                    placeName = places.map { it.name },
//                    placeId = places.map { it.placeId },
//                    photoUrlList = places.map {
//                        it.getDestinationPhotoUrl()
//                            ?: "https://plus.unsplash.com/premium_photo-1725943391552-ac450c795074?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
//                    },
//                    placeRating = places.map { it.rating },
//                    placeTotalRatings = places.map { it.totalRatings },
//                    onCardClick = { placeId, _, _ ->
//                        navController.navigate(
//                            ScreenNavigation.DestinationFullDetails.passArgs(
//                                placeId = placeId
//                            )
//                        )
//                    }
//                )
//            } else {
//                Text(text="No hotels found!!")
//            }
//        }
//        result.isFailure -> {
//            Text(text="No hotels found!!")
//        }
//        else -> {
//            CircularProgressIndicator()
//        }
//    }
//}
//
///// carousel end -------

