package com.example.travista.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.travista.R
import kotlinx.coroutines.delay

// for lazy row contains place card composable
@Composable
fun PlacesCarousel(
    placeName: List<String?> = emptyList(),
    placeId: List<String?> = emptyList(),
    photoUrlList: List<String?> = emptyList(),
    placeAddress: List<String?> = emptyList(),
    placeRating: List<Double?> = emptyList(),
    placeTotalRatings: List<Int?> = emptyList(),
    descriptionList: List<String?> = emptyList(),
    extraInfoList: List<String?> = emptyList(),
    onCardClick: (placeId: String, name: String, address: String?) -> Unit = { _, _, _ -> }
) {
    val maxSize = listOf(
        placeName.size,
        placeId.size,
        photoUrlList.size,
        placeAddress.size,
        placeRating.size,
        placeTotalRatings.size,
        descriptionList.size,
        extraInfoList.size
    ).maxOrNull() ?: 0

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(maxSize) { index ->
            val name = placeName.getOrNull(index)?.takeIf { !it.isNullOrBlank() } ?: return@items
            val id = placeId.getOrNull(index)?.takeIf { !it.isNullOrBlank() } ?: return@items
            val imageUrl = photoUrlList.getOrNull(index)?.takeIf { !it.isNullOrBlank() }
                ?: "https://images.unsplash.com/photo-1583578568273-a1a95c9a5b23?q=80&w=1976&auto=format&fit=crop"

            val address = placeAddress.getOrNull(index)?.takeIf { !it.isNullOrBlank() }
            val rating = placeRating.getOrNull(index)?.takeIf { it != 0.0 }?.toString()
            val totalRatings = placeTotalRatings.getOrNull(index)?.takeIf { it != 0 }?.toString()
            val description = descriptionList.getOrNull(index)?.takeIf { !it.isNullOrBlank() }
            val extraInfo = extraInfoList.getOrNull(index)?.takeIf { !it.isNullOrBlank() }

            PlaceCard(
                placeName = name,
                photoUrl = imageUrl,
                onClick = { onCardClick(id, name, address) },
                address = address,
                rating = rating,
                totalRatings = totalRatings,
                description = description,
                extraInfo = extraInfo
            )
        }
    }
}


// place card composable
@Composable
fun PlaceCard(
    placeName: String,
    photoUrl: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    address: String? = null,
    rating: String? = null,
    totalRatings: String? = null,
    description: String? = null,
    extraInfo: String? = null
) {
    var isFavorite by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.width(180.dp)) {
        Box(
            modifier = Modifier
                .height(250.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Gray)
                .clickable { onClick() }
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUrl)
                    .crossfade(true)
                    .error(R.drawable.default_background_image)
                    .build(),
                contentDescription = "Place Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(if (isFavorite) Color.Red else Color.White.copy(alpha = 0.9f))
                    .clickable { isFavorite = !isFavorite },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.White else Color.Black,
                    modifier = Modifier.size(25.dp)
                )
            }
        }

        // Always show place name
        Text(
            text = placeName,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        address?.let {
            Text(text = it, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }

        rating?.let {
            Text(text = "Rating: $it ⭐", style = MaterialTheme.typography.labelSmall)
        }

        totalRatings?.let {
            Text(text = "Reviews: $it", style = MaterialTheme.typography.labelSmall)
        }

        description?.let {
            Text(text = it, style = MaterialTheme.typography.bodySmall, maxLines = 2)
        }

        extraInfo?.let {
            Text(text = it, style = MaterialTheme.typography.bodySmall, maxLines = 2)
        }
    }
}













// Composable for displaying list of images using horizontal pager

@Composable
fun ImageCarousel(
    imageUrls: List<String>,
    modifier: Modifier = Modifier,

    autoScroll: Boolean = true  // Auto-scroll feature

) {
    val pagerState = rememberPagerState(
        pageCount = { imageUrls.size },
        initialPage = 0
    )

    // Auto-scroll effect
    LaunchedEffect(pagerState, autoScroll) {
        if (autoScroll) {
            while (true) {
                delay(3000)  // Delay of 3 seconds before moving to the next image
                val nextPage = (pagerState.currentPage + 1) % imageUrls.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),

                ) { page ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrls[page])
                        .crossfade(true)
                        .error(R.drawable.default_background_image) // Default image for errors
                        .build(),
                    contentDescription = "Restaurant Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )

            }

            // Image Counter (Top-Right Corner)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${pagerState.currentPage + 1} / ${imageUrls.size}",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }


    }
}

//lazy row composable for top destinations
@Composable
fun TopPlacesCarousel(
    placeName: List<String?> = listOf(
        " ",
        null),
    placeId: List<String?> = listOf(null, "", "12345", null),
    photoUrlList: List<String?> = listOf(null, "", "https://example.com/image.jpg", null),
    placeAddress: List<String?> = listOf(null, "123 Main St", "456 Elm St", null),

    ) {
    val maxSize = maxOf(placeName.size, placeId.size, photoUrlList.size)
    // Get the largest list size



    LazyRow(
        modifier = Modifier.fillMaxWidth().animateContentSize(),

        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(maxSize) { index -> // Iterate safely
            val item1 = placeName.getOrNull(index) ?: "Unknown Place" // Handle null names
            val item2 = placeId.getOrNull(index) ?: "N/A" // Handle null place IDs
            val item3 = photoUrlList.getOrNull(index)
                ?: "https://images.unsplash.com/photo-1583578568273-a1a95c9a5b23?q=80&w=1976&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" // Handle null image URLs
            val item4 = placeAddress.getOrNull(index) ?: "N/A" // Handle null addresses


            var isFavorite by rememberSaveable { mutableStateOf(false) } // Track favorite state

            Column() {
                Box(

                    modifier = Modifier
                        .width(if (maxSize<=2) Int.MAX_VALUE.dp else 180.dp)
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray)
                        .clickable {

                        }
                ) {
                    // Show image inside the box
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item3) // Use item3 (photo URL)
                            .crossfade(true)
                            .error(R.drawable.default_background_image) // Show default image if loading fails
                            .build(),
                        contentDescription = "Scrolling background image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Favorite Icon (Heart)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(if (isFavorite) Color.Red else Color.White.copy(alpha = 0.9f))
                            .clickable { isFavorite = !isFavorite },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.White else Color.Black,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }

                // Place name under the card
                Text(
                    text = item1,
                    modifier = Modifier
                        .width(180.dp)
                        .padding(top = 4.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                    ,maxLines =  2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item4, modifier = Modifier

                        .width(180.dp),
                    maxLines =  2,
                    overflow = TextOverflow.Ellipsis
                )

            }
        }
    }

}



//
//// top destinations carousel
//
//@Composable
//fun ShowTopPlacesCarousel(result: Result<List<TopDestinations>>) {
//    when {
//        result.isSuccess -> {
//            val places = result.getOrNull().orEmpty()
//            if (places.isNotEmpty()) {
//                TopPlacesCarousel(
//                    placeName = places.map { it.name },
//                    placeId = places.map { it.placeId },
//                    photoUrlList = places.map { it.getDestinationPhotoUrl() ?: "https://plus.unsplash.com/premium_photo-1725943391552-ac450c795074?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" },
//                    placeAddress = places.map { it.address }
//                )
//            } else {
//                Text(text="No destinations found")
//            }
//        }
//        result.isFailure -> {
//            Text(text="No destinations found!!")
//        }
//        else -> {
//            CircularProgressIndicator()
//        }
//    }
//}


