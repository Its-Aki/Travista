package com.example.travista.screens.home

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.travista.R
import com.example.travista.components.ImageCarousel
import com.example.travista.components.LoadingLottieAnimation
import com.example.travista.data.detinationfulldetails.getPriceLevelText
import com.example.travista.data.getApikey
import com.example.travista.viewmodel.DestinationFullDetailsViewModel
import com.example.travista.utils.isNetworkAvailable


@Composable
fun DestinationFullDetailsScreen(
    placeId: String,
    apiKey: String = getApikey(),
    viewModel: DestinationFullDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val destinationState by viewModel.destinationFullDetails.collectAsState()
    val scrollState = rememberScrollState()

    val showLoading = destinationState == null && isNetworkAvailable(context)

    //  Only call API if not already loaded and internet is available
    LaunchedEffect(placeId, apiKey, destinationState) {
        if (destinationState == null && isNetworkAvailable(context)) {
            viewModel.fetchDestinationFullDetails(placeId, apiKey)
        } else if (!isNetworkAvailable(context) && destinationState == null) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    if (showLoading) {
        LoadingLottieAnimation()
    } else {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
            when {
                destinationState == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                destinationState!!.isFailure -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "Error: ${destinationState!!.exceptionOrNull()?.message}",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                destinationState!!.isSuccess -> {
                    val destinationFullDetails = destinationState!!.getOrNull()
                    destinationFullDetails?.let {
                        val defaultImageUrls = listOf(
                            "https://images.unsplash.com/photo-1726044781946-fca0baa0cdcd?q=80&w=715&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                            "https://images.unsplash.com/photo-1708366790875-30cedd9e8861?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                            "https://images.unsplash.com/photo-1644490771655-267115aa235c?q=80&w=735&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                            "https://images.unsplash.com/photo-1634351355990-b177bd6d8288?q=80&w=651&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                            "https://images.unsplash.com/photo-1540845692348-b9d2bc813a63?q=80&w=770&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
                        )

                        val photoUrls = it.photos?.mapNotNull { photo ->
                            photo.photoReference?.let { reference ->
                                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$reference&key=$apiKey"
                            }
                        } ?: emptyList()

                        if (photoUrls.isEmpty()) {
                            ImageCarousel(imageUrls = defaultImageUrls)
                        } else {
                            ImageCarousel(imageUrls = photoUrls)
                        }

                        Column(Modifier.padding(horizontal = 6.dp, vertical = 8.dp)) {
                            // Header Section
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                                    Text(
                                        it.name ?: "Unknown Place",
                                        fontWeight = Bold,
                                        fontSize = 28.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = "Location",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            it.address ?: "Address not available",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 14.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Rating",
                                            modifier = Modifier.size(20.dp),
                                            tint = Color(0xFFFFB300)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "Rating: ${it.rating ?: "N/A"}",
                                            fontWeight = Bold,
                                            fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "(${it.totalRatings ?: 0} reviews)",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Contact Info Section
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Language,
                                                    contentDescription = "Website",
                                                    modifier = Modifier.size(16.dp),
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    "Website:",
                                                    fontWeight = Bold,
                                                    fontSize = 14.sp
                                                )
                                            }
                                            Text(
                                                it.website ?: "Not available",
                                                fontSize = 13.sp,
                                                color = if (it.website != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier
                                                    .padding(start = 22.dp)
                                                    .then(
                                                        if (!it.website.isNullOrBlank()) {
                                                            Modifier.clickable {
                                                                try {
                                                                    val websiteUrl = if (!it.website.startsWith("http://") && !it.website.startsWith("https://")) {
                                                                        "https://${it.website}"
                                                                    } else {
                                                                        it.website
                                                                    }
                                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
                                                                    context.startActivity(intent)
                                                                } catch (e: Exception) {
                                                                    Toast.makeText(context, "Unable to open website", Toast.LENGTH_SHORT).show()
                                                                }
                                                            }
                                                        } else Modifier
                                                    )
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Call,
                                                    contentDescription = "Phone",
                                                    modifier = Modifier.size(16.dp),
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    "Call:",
                                                    fontWeight = Bold,
                                                    fontSize = 14.sp
                                                )
                                            }
                                            Text(
                                                it.phoneNumber ?: "Not available",
                                                fontSize = 13.sp,
                                                color = if (it.phoneNumber != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier
                                                    .padding(start = 22.dp)
                                                    .then(
                                                        if (!it.phoneNumber.isNullOrBlank()) {
                                                            Modifier.clickable {
                                                                try {
                                                                    val phoneNumber = it.phoneNumber.replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
                                                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                                                                    context.startActivity(intent)
                                                                } catch (e: Exception) {
                                                                    Toast.makeText(context, "Unable to open dialer", Toast.LENGTH_SHORT).show()
                                                                }
                                                            }
                                                        } else Modifier
                                                    )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.AttachMoney,
                                            contentDescription = "Price",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "Price Level: ${getPriceLevelText(it.priceLevel)}",
                                            fontWeight = Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Opening Hours Section
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.AccessTime,
                                            contentDescription = "Hours",
                                            modifier = Modifier.size(18.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Opening Hours",
                                            fontWeight = Bold,
                                            fontSize = 16.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(
                                                    color = if (it.openingHours?.isOpenNow == true)
                                                        Color(0xFF4CAF50) else Color(0xFFF44336),
                                                    shape = CircleShape
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        if (it.openingHours?.isOpenNow == true) {
                                            Text(
                                                "Open Now: Yes",
                                                fontWeight = Bold,
                                                color = Color(0xFF4CAF50)
                                            )
                                        } else {
                                            Text(
                                                "Open Now: No",
                                                fontWeight = Bold,
                                                color = Color(0xFFF44336)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    if (it.openingHours != null) {
                                        Text(it.openingHours.weekdayText?.joinToString("\n") ?: "Hours not available")
                                    } else {
                                        Text("Open Hours: Not Available", fontWeight = Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Tags Section
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                                    Text("Tags:", fontWeight = Bold, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    if (!it.types.isNullOrEmpty()) {
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            items(it.types) { tag ->
                                                Card(
                                                    modifier = Modifier.wrapContentSize(),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                                    ),
                                                    shape = RoundedCornerShape(20.dp)
                                                ) {
                                                    Text(
                                                        text = tag.replace("_", " ").split(" ").joinToString(" ") { word ->
                                                            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                                                        },
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Text("No tags available")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    try {
                                        val mapsUrl = it.googleMapsUrl
                                        if (!mapsUrl.isNullOrBlank()) {
                                            val intent = Intent(Intent.ACTION_VIEW, mapsUrl.toUri())
                                            intent.setPackage("com.google.android.apps.maps")
                                            context.startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "Google Maps URL not available", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        // Fallback: try opening without specific package
                                        try {
                                            val mapsUrl = it.googleMapsUrl
                                            if (!mapsUrl.isNullOrBlank()) {
                                                val intent = Intent(Intent.ACTION_VIEW, mapsUrl.toUri())
                                                context.startActivity(intent)
                                            } else {
                                                Toast.makeText(context, "Google Maps URL not available", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (ex: Exception) {
                                            Toast.makeText(context, "Unable to open Google Maps", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Open in Google Maps", fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = {
                                    try {
                                        val placeName = it.name ?: "Unknown Place"
                                        val placeAddress = it.address ?: ""
                                        val mapsUrl =
                                            "https://www.google.com/maps/dir/?api=1&destination=${
                                                Uri.encode("$placeName,$placeAddress")
                                            }"
                                        val intent = Intent(Intent.ACTION_VIEW, mapsUrl.toUri())
                                        intent.setPackage("com.google.android.apps.maps")
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // Fallback: try opening without specific package
                                        try {
                                            val placeName = it.name ?: "Unknown Place"
                                            val placeAddress = it.address ?: ""
                                            val mapsUrl =
                                                "https://www.google.com/maps/dir/?api=1&destination=${
                                                    Uri.encode("$placeName,$placeAddress")
                                                }"
                                            val intent = Intent(Intent.ACTION_VIEW, mapsUrl.toUri())
                                            context.startActivity(intent)
                                        } catch (ex: Exception) {
                                            Toast.makeText(context, "Unable to get directions", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Get Directions", fontSize = 16.sp)
                            }

                            Spacer(Modifier.padding(30.dp))

                            if (it.reviews.isNullOrEmpty()) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        "No reviews available",
                                        fontWeight = Bold,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                                        fontSize = 16.sp
                                    )
                                }
                            } else {
                                Text(
                                    text = "Reviews:",
                                    fontWeight = Bold,
                                    fontSize = 24.sp
                                )
                                Spacer(Modifier.padding(12.dp))

                                it.reviews.forEach { review ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 4.dp, vertical = 6.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                                            Row(
                                                Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(context)
                                                        .data(review.profileImageUrl)
                                                        .crossfade(true)
                                                        .error(R.drawable.default_background_image1)
                                                        .build(),
                                                    contentDescription = "Profile Picture",
                                                    modifier = Modifier
                                                        .size(50.dp)
                                                        .clip(CircleShape),
                                                    contentScale = ContentScale.Crop
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(
                                                        " ${review.authorName ?: "Anonymous"}",
                                                        fontWeight = Bold,
                                                        fontSize = 16.sp
                                                    )
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        val rating = when (val r = review.rating) {
                                                            is Int -> r
                                                            is Double -> r.toInt()
                                                            is Float -> r.toInt()
                                                            else -> 0
                                                        }
                                                        (1..5).forEach { index ->
                                                            Icon(
                                                                imageVector = Icons.Default.Star,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(14.dp),
                                                                tint = if (index <= rating) Color(0xFFFFB300)
                                                                else MaterialTheme.colorScheme.outline
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            "${review.rating ?: 0}",
                                                            fontSize = 12.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))

                                            Text(
                                                review.reviewText ?: "No review text available",
                                                lineHeight = 20.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedButton(
                                    onClick = {
                                        try {
                                            val mapsUrl = it.googleMapsUrl
                                            if (!mapsUrl.isNullOrBlank()) {
                                                val intent = Intent(Intent.ACTION_VIEW, mapsUrl.toUri())
                                                intent.setPackage("com.google.android.apps.maps")
                                                context.startActivity(intent)
                                            } else {
                                                Toast.makeText(context, "Google Maps URL not available", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            // Fallback: try opening without specific package
                                            try {
                                                val mapsUrl = it.googleMapsUrl
                                                if (!mapsUrl.isNullOrBlank()) {
                                                    val intent = Intent(Intent.ACTION_VIEW, mapsUrl.toUri())
                                                    context.startActivity(intent)
                                                } else {
                                                    Toast.makeText(context, "Google Maps URL not available", Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (ex: Exception) {
                                                Toast.makeText(context, "Unable to open Google Maps", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("See all reviews", fontSize = 16.sp)
                                }

                                Spacer(Modifier.padding(80.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ShowHotelDetails() {
    // Preview content if needed
}