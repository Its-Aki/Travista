package com.example.travista.components


import android.R.attr.description
import android.R.attr.onClick
import android.R.attr.rating
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.Coil.imageLoader
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.travista.R
import kotlinx.coroutines.delay

//  Carousel for top places in the home screen -- start

@Composable
fun TopPlacesCarousel(
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

    val listState = rememberLazyListState()

    //  Use derivedStateOf to safely read layoutInfo
    val context = LocalContext.current
    val imageLoader: ImageLoader = remember {
        ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        state = listState
    ) {
        items(maxSize) { index ->
            val name = placeName.getOrNull(index)?.takeIf { it.isNotBlank() } ?: return@items
            val id = placeId.getOrNull(index)?.takeIf { it.isNotBlank() } ?: return@items
            val imageUrl = photoUrlList.getOrNull(index)?.takeIf { it.isNotBlank() }
                ?: "https://images.unsplash.com/photo-1583578568273-a1a95c9a5b23?q=80&w=1976&auto=format&fit=crop"
            val address = placeAddress.getOrNull(index)?.takeIf { it.isNotBlank() }
            val rating = placeRating.getOrNull(index)?.takeIf { it != 0.0 }?.toString()
            val totalRatings = placeTotalRatings.getOrNull(index)?.takeIf { it != 0 }?.toString()
            val description = descriptionList.getOrNull(index)?.takeIf { it.isNotBlank() }
            val extraInfo = extraInfoList.getOrNull(index)?.takeIf { it.isNotBlank() }

            Box(modifier = Modifier) {
                TopPlaceCard(
                    placeName = name,
                    photoUrl = imageUrl,
                    onClick = { onCardClick(id, name, address) },
                    address = address,
                    rating = rating,
                    totalRatings = totalRatings,
                    description = description,
                    extraInfo = extraInfo,
                    isSingleItem = maxSize == 1,
                    imageLoader = imageLoader
                )
            }
        }
    }
}

// place card composable with overlay effect
@Composable
fun TopPlaceCard(
    placeName: String,
    photoUrl: String,
    onClick: () -> Unit,
    address: String? = null,
    rating: String? = null,
    totalRatings: String? = null,
    description: String? = null,
    extraInfo: String? = null,
    isSingleItem: Boolean = false,
    imageLoader: ImageLoader
) {
    val screenWidth =
        LocalContext.current.resources.displayMetrics.widthPixels / LocalContext.current.resources.displayMetrics.density

    // Hold detection
    var isHeld by remember { mutableStateOf(false) }

    // Animate scale based on hold state
    val scale by animateFloatAsState(
        targetValue = if (isHeld) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.9f, stiffness = 30f),
        label = "CardHoldScale"
    )

    Box(
        modifier = if (isSingleItem) Modifier
            .width(screenWidth.dp)
            .padding(end = 4.dp) else Modifier.width(280.dp)
    ) {
        Box(
            modifier = Modifier
                .scale(scale)
                .height(280.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Gray)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            isHeld = true
                        },
                        onPress = {
                            isHeld = true
                            tryAwaitRelease()
                            isHeld = false
                        },
                        onTap = {
                            onClick()
                        }
                    )
                }
        ) {
            // Background Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUrl)
                    .crossfade(true)
                    .placeholder(R.drawable.default_background_image1)
                    .error(R.drawable.default_background_image1)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "Place Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dark overlay gradient for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            // Text overlay at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = placeName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                address?.let {
                    Text(
                        text = it,
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Optional: Show rating if available
                rating?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "⭐ $it",
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.labelSmall
                        )
                        totalRatings?.let { total ->
                            Text(
                                text = " ($total)",
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}


//  Carousel for top places in the home screen  --- end

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

    val listState = rememberLazyListState()

    //  Use derivedStateOf to safely read layoutInfo
    val center by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
        }
    }
    val context = LocalContext.current
    val imageLoader: ImageLoader = remember {
        ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }


    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
        state = listState
    ) {
        items(maxSize) { index ->
            val name = placeName.getOrNull(index)?.takeIf { it.isNotBlank() } ?: return@items
            val id = placeId.getOrNull(index)?.takeIf { it.isNotBlank() } ?: return@items
            val imageUrl = photoUrlList.getOrNull(index)?.takeIf { it.isNotBlank() }
                ?: "https://images.unsplash.com/photo-1583578568273-a1a95c9a5b23?q=80&w=1976&auto=format&fit=crop"
            val address = placeAddress.getOrNull(index)?.takeIf { it.isNotBlank() }
            val rating = placeRating.getOrNull(index)?.takeIf { it != 0.0 }?.toString()
            val totalRatings = placeTotalRatings.getOrNull(index)?.takeIf { it != 0 }?.toString()
            val description = descriptionList.getOrNull(index)?.takeIf { it.isNotBlank() }
            val extraInfo = extraInfoList.getOrNull(index)?.takeIf { it.isNotBlank() }

            Box(modifier = Modifier) {
                PlaceCard(
                    placeName = name,
                    photoUrl = imageUrl,
                    onClick = { onCardClick(id, name, address) },
                    address = address,
                    rating = rating,
                    totalRatings = totalRatings,
                    description = description,
                    extraInfo = extraInfo,
                    isSingleItem = maxSize == 1,
                    imageLoader = imageLoader
                )
            }
        }
    }
}





// place card composable
@Composable
fun PlaceCard(
    placeName: String,
    photoUrl: String,
    onClick: () -> Unit,
    address: String? = null,
    rating: String? = null,
    totalRatings: String? = null,
    description: String? = null,
    extraInfo: String? = null,
    isSingleItem: Boolean = false,
    imageLoader: ImageLoader
) {
    val screenWidth =
        LocalContext.current.resources.displayMetrics.widthPixels / LocalContext.current.resources.displayMetrics.density
    var isFavorite by rememberSaveable { mutableStateOf(false) }

    // Hold detection
    var isHeld by remember { mutableStateOf(false) }

    // Animate scale based on hold state
    val scale by animateFloatAsState(
        targetValue = if (isHeld) 0.95f else 1f,
        animationSpec = spring( stiffness = 40f),
        label = "CardHoldScale"
    )

    // Parse rating as float for star calculation
    val ratingValue = rating?.toFloatOrNull() ?: 0f

    // Display total ratings with fallback to "0"
    val displayTotalRatings = totalRatings ?: "0"

    // Simple Column without Card container - scales together
    Column(
        modifier = if (isSingleItem)
            Modifier
                .width(screenWidth.dp)
                .padding(end = 4.dp)
                .scale(scale)
        else
            Modifier
                .width(180.dp)
                .scale(scale)
    ) {
        // Rounded Image Section (same as original design)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Gray)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            isHeld = true
                        },
                        onPress = {
                            isHeld = true
                            tryAwaitRelease()
                            isHeld = false
                        },
                        onTap = {
                            onClick()
                        }
                    )
                }
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUrl)
                    .crossfade(true)
                    .placeholder(R.drawable.default_background_image1)
                    .error(R.drawable.default_background_image1)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "Place Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Heart/Favorite Icon - kept exactly as original with separate click handling
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

        // Fixed height container for all text content to prevent jumping
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp) // Fixed height for text content
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) { onClick() } // Make entire text area clickable
        ) {
            // Place Name - natural height based on content
            Text(
                text = placeName,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Star Rating Display
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                StarRating(rating = ratingValue, starSize = 14.dp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format("%.1f", ratingValue),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            // Total Ratings - natural height, always show
            Text(
                text = "Reviews: $displayTotalRatings",
                style = MaterialTheme.typography.labelSmall
            )

            // Optional fields in remaining space
            address?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            extraInfo?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun StarRating(
    rating: Float,
    maxStars: Int = 5,
    starSize: Dp = 16.dp,
    filledColor: Color = Color(0xFFFFC107), // Bright yellow color
    emptyColor: Color = Color.Gray.copy(alpha = 0.3f)
) {
    // Calculate number of full stars and whether to show half star
    val fullStars = rating.toInt()
    val hasHalfStar = rating % 1 != 0f // Check if there's a decimal part

    Row {
        repeat(maxStars) { index ->
            when {
                index < fullStars -> {
                    // Full star - for ratings like 3.2, show 3 full stars
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Full Star",
                        tint = filledColor,
                        modifier = Modifier.size(starSize)
                    )
                }
                index == fullStars && hasHalfStar -> {
                    // Half star - for ratings like 3.2, show half star as the 4th star
                    Box(modifier = Modifier.size(starSize)) {
                        // Empty star background
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = "Empty Star",
                            tint = emptyColor,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Half filled star using clip
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Half Star",
                            tint = filledColor,
                            modifier = Modifier
                                .fillMaxSize()
                                .clipToBounds()
                                .drawWithContent {
                                    clipRect(right = size.width * 0.5f) {
                                        this@drawWithContent.drawContent()
                                    }
                                }
                        )
                    }
                }
                else -> {
                    // Empty star
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "Empty Star",
                        tint = emptyColor,
                        modifier = Modifier.size(starSize)
                    )
                }
            }
        }
    }
}


/// Backup in case of errors ---- Don't Delete ****

//// for lazy row contains place card composable
//
//@Composable
//fun PlacesCarousel(
//    placeName: List<String?> = emptyList(),
//    placeId: List<String?> = emptyList(),
//    photoUrlList: List<String?> = emptyList(),
//    placeAddress: List<String?> = emptyList(),
//    placeRating: List<Double?> = emptyList(),
//    placeTotalRatings: List<Int?> = emptyList(),
//    descriptionList: List<String?> = emptyList(),
//    extraInfoList: List<String?> = emptyList(),
//    onCardClick: (placeId: String, name: String, address: String?) -> Unit = { _, _, _ -> }
//) {
//    val maxSize = listOf(
//        placeName.size,
//        placeId.size,
//        photoUrlList.size,
//        placeAddress.size,
//        placeRating.size,
//        placeTotalRatings.size,
//        descriptionList.size,
//        extraInfoList.size
//    ).maxOrNull() ?: 0
//
//    val listState = rememberLazyListState()
//
//    //  Use derivedStateOf to safely read layoutInfo
//    val center by remember {
//        derivedStateOf {
//            val layoutInfo = listState.layoutInfo
//            layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
//        }
//    }
//    val context = LocalContext.current
//    val imageLoader: ImageLoader = remember {
//        ImageLoader.Builder(context)
//            .crossfade(true)
//            .memoryCachePolicy(CachePolicy.ENABLED)
//            .diskCachePolicy(CachePolicy.ENABLED)
//            .build()
//    }
//
//
//    LazyRow(
//        modifier = Modifier
//            .fillMaxWidth()
//            .animateContentSize(),
//        horizontalArrangement = Arrangement.spacedBy(12.dp),
//        state = listState
//    ) {
//        items(maxSize) { index ->
//            val name = placeName.getOrNull(index)?.takeIf { it.isNotBlank() } ?: return@items
//            val id = placeId.getOrNull(index)?.takeIf { it.isNotBlank() } ?: return@items
//            val imageUrl = photoUrlList.getOrNull(index)?.takeIf { it.isNotBlank() }
//                ?: "https://images.unsplash.com/photo-1583578568273-a1a95c9a5b23?q=80&w=1976&auto=format&fit=crop"
//            val address = placeAddress.getOrNull(index)?.takeIf { it.isNotBlank() }
//            val rating = placeRating.getOrNull(index)?.takeIf { it != 0.0 }?.toString()
//            val totalRatings = placeTotalRatings.getOrNull(index)?.takeIf { it != 0 }?.toString()
//            val description = descriptionList.getOrNull(index)?.takeIf { it.isNotBlank() }
//            val extraInfo = extraInfoList.getOrNull(index)?.takeIf { it.isNotBlank() }
//
//            Box(modifier = Modifier) {
//                PlaceCard(
//                    placeName = name,
//                    photoUrl = imageUrl,
//                    onClick = { onCardClick(id, name, address) },
//                    address = address,
//                    rating = rating,
//                    totalRatings = totalRatings,
//                    description = description,
//                    extraInfo = extraInfo,
//                    isSingleItem = maxSize == 1,
//                    imageLoader = imageLoader
//                )
//            }
//        }
//    }
//}
//
//
//
//
//
//// place card composable
//@Composable
//fun PlaceCard(
//    placeName: String,
//    photoUrl: String,
//    onClick: () -> Unit,
//    address: String? = null,
//    rating: String? = null,
//    totalRatings: String? = null,
//    description: String? = null,
//    extraInfo: String? = null,
//    isSingleItem: Boolean = false,
//    imageLoader: ImageLoader
//
//) {
//    val screenWidth =
//        LocalContext.current.resources.displayMetrics.widthPixels / LocalContext.current.resources.displayMetrics.density
//    var isFavorite by rememberSaveable { mutableStateOf(false) }
//
//    // Hold detection
//    var isHeld by remember { mutableStateOf(false) }
//
//    // Animate scale based on hold state
//    val scale by animateFloatAsState(
//        targetValue = if (isHeld) 0.95f else 1f,
//        animationSpec = spring(dampingRatio = 0.9f, stiffness = 30f),
//        label = "CardHoldScale"
//    )
//    // image loader for entire composable to avoid creating multiple for each card
//
//
//    Column(
//        modifier = if (isSingleItem) Modifier
//            .width(screenWidth.dp)
//            .padding(end = 4.dp) else Modifier.width(180.dp)
//    ) {
//        Box(
//            modifier = Modifier
//                .scale(scale)
//                .height(250.dp)
//                .clip(RoundedCornerShape(20.dp))
//                .background(Color.Gray)
//                .pointerInput(Unit) {
//                    detectTapGestures(
//                        onLongPress = {
//                            isHeld = true
//                        },
//                        onPress = {
//                            isHeld = true
//                            tryAwaitRelease()
//                            isHeld = false
//                        },
//                        onTap = {
//                            onClick()
//                        }
//                    )
//                }
//        ) {
//            AsyncImage(
//                model = ImageRequest.Builder(LocalContext.current)
//                    .data(photoUrl)
//                    .crossfade(true)
//                    .placeholder(R.drawable.default_background_image1)
//                    .error(R.drawable.default_background_image1)
//                    .build(),
//                imageLoader = imageLoader, //  Use shared imageLoader
//                contentDescription = "Place Image",
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Crop
//            )
//            Box(
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .padding(8.dp)
//                    .size(30.dp)
//                    .clip(CircleShape)
//                    .background(if (isFavorite) Color.Red else Color.White.copy(alpha = 0.9f))
//                    .clickable { isFavorite = !isFavorite },
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
//                    contentDescription = "Favorite",
//                    tint = if (isFavorite) Color.White else Color.Black,
//                    modifier = Modifier.size(25.dp)
//                )
//            }
//
//        }
//
//
//        Text(
//            text = placeName,
//            fontWeight = FontWeight.Bold,
//            style = MaterialTheme.typography.bodyLarge,
//            maxLines = 2,
//            overflow = TextOverflow.Ellipsis,
//            modifier = Modifier.padding(top = 4.dp)
//        )
//
//        address?.let {
//            Text(
//                text = it,
//                style = MaterialTheme.typography.bodySmall,
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis
//            )
//        }
//
//        rating?.let {
//            Text(text = "Rating: $it ⭐", style = MaterialTheme.typography.labelSmall)
//        }
//
//        totalRatings?.let {
//            Text(text = "Reviews: $it", style = MaterialTheme.typography.labelSmall)
//        }
//
//        description?.let {
//            Text(text = it, style = MaterialTheme.typography.bodySmall, maxLines = 2)
//        }
//
//        extraInfo?.let {
//            Text(text = it, style = MaterialTheme.typography.bodySmall, maxLines = 2)
//        }
//
//
//
//    }
//}
//

/// Backup code above in case of failures. Don't Delete ****

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
                            .error(R.drawable.default_background_image1) // Default image for errors or while loading
                            .build(),
                        contentDescription = "Restaurant Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    )

                }

                // Image Counter (Top-Right Corner)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp)
                        )
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

// loading lottie animation


    @Composable
    fun LoadingLottieAnimation() {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.Asset("loading_lottie.json")
        )
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever
        )

        LottieAnimation(
            composition,
            { progress },
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        )
    }




//UI for carousel in column style using the previous composable places carousel

@Composable
fun VerticalPlacesCarousel(
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

    val context = LocalContext.current
    val imageLoader: ImageLoader = remember {
        ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        for (index in 0 until maxSize) {
            val name = placeName.getOrNull(index)?.takeIf { it.isNotBlank() } ?: continue
            val id = placeId.getOrNull(index)?.takeIf { it.isNotBlank() } ?: continue
            val imageUrl = photoUrlList.getOrNull(index)?.takeIf { it.isNotBlank() }
                ?: "https://images.unsplash.com/photo-1583578568273-a1a95c9a5b23?q=80&w=1976&auto=format&fit=crop"
            val address = placeAddress.getOrNull(index)?.takeIf { it.isNotBlank() }
            val rating = placeRating.getOrNull(index)?.takeIf { it != 0.0 }?.toString()
            val totalRatings = placeTotalRatings.getOrNull(index)?.takeIf { it != 0 }?.toString()
            val description = descriptionList.getOrNull(index)?.takeIf { it.isNotBlank() }
            val extraInfo = extraInfoList.getOrNull(index)?.takeIf { it.isNotBlank() }

            Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                VerticalPlaceCard(
                    placeName = name,
                    photoUrl = imageUrl,
                    onClick = { onCardClick(id, name, address) },
                    address = address,
                    rating = rating,
                    totalRatings = totalRatings,
                    description = description,
                    extraInfo = extraInfo,
                    imageLoader = imageLoader
                )
            }
        }
    }
}

// Enhanced vertical placeCard with improved UI
@Composable
fun VerticalCarouselStarRating(
    rating: Float,
    maxStars: Int = 5,
    starSize: Dp = 16.dp,
    filledColor: Color = Color(0xFFFFC107), // Bright yellow color
    emptyColor: Color = Color.Gray.copy(alpha = 0.3f)
) {
    // Calculate number of full stars and whether to show half star
    val fullStars = rating.toInt()
    val hasHalfStar = rating % 1 != 0f // Check if there's a decimal part

    Row {
        repeat(maxStars) { index ->
            when {
                index < fullStars -> {
                    // Full star - for ratings like 3.2, show 3 full stars
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Full Star",
                        tint = filledColor,
                        modifier = Modifier.size(starSize)
                    )
                }
                index == fullStars && hasHalfStar -> {
                    // Half star - for ratings like 3.2, show half star as the 4th star
                    Box(modifier = Modifier.size(starSize)) {
                        // Empty star background
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = "Empty Star",
                            tint = emptyColor,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Half filled star using clip
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Half Star",
                            tint = filledColor,
                            modifier = Modifier
                                .fillMaxSize()
                                .clipToBounds()
                                .drawWithContent {
                                    clipRect(right = size.width * 0.5f) {
                                        this@drawWithContent.drawContent()
                                    }
                                }
                        )
                    }
                }
                else -> {
                    // Empty star
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "Empty Star",
                        tint = emptyColor,
                        modifier = Modifier.size(starSize)
                    )
                }
            }
        }
    }
}

@Composable
fun VerticalPlaceCard(
    placeName: String,
    photoUrl: String,
    onClick: () -> Unit,
    address: String? = null,
    rating: String? = null,
    totalRatings: String? = null,
    description: String? = null,
    extraInfo: String? = null,
    imageLoader: ImageLoader
) {
    var isFavorite by rememberSaveable { mutableStateOf(false) }

    // Hold detection
    var isHeld by remember { mutableStateOf(false) }

    // Animate scale based on hold state
    val scale by animateFloatAsState(
        targetValue = if (isHeld) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.9f, stiffness = 30f),
        label = "CardHoldScale"
    )

    // Parse rating as float for star calculation
    val ratingValue = rating?.toFloatOrNull() ?: 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            // Image section
            Row(Modifier) {
                Box(
                    modifier = Modifier
                        .height(150.dp)
                        .width(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    isHeld = true
                                },
                                onPress = {
                                    isHeld = true
                                    tryAwaitRelease()
                                    isHeld = false
                                },
                                onTap = {
                                    onClick()
                                }
                            )
                        }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(photoUrl)
                            .crossfade(true)
                            .placeholder(R.drawable.default_background_image1)
                            .error(R.drawable.default_background_image1)
                            .build(),
                        imageLoader = imageLoader,
                        contentDescription = "Place Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Subtle gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.1f)
                                    )
                                )
                            )
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isFavorite)
                                    Color.Red
                                else
                                    Color.White.copy(alpha = 0.95f)
                            )
                            .clickable { isFavorite = !isFavorite },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.White else Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Place name with better typography
                Text(
                    text = placeName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Address with location icon
                address?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Rating with custom star display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    VerticalCarouselStarRating(rating = ratingValue, starSize = 16.dp)
                    Text(
                        text = String.format("%.1f", ratingValue),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                // Reviews with review icon
                if (totalRatings == null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.RateReview,
                            contentDescription = "Reviews",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "0 reviews",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
                totalRatings?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.RateReview,
                            contentDescription = "Reviews",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$it reviews",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                extraInfo?.let {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/// for all places when clicked on things like all hotels,all restaurants, etc
@Composable
fun AllPlacesCarousel(
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

    val context = LocalContext.current
    val imageLoader: ImageLoader = remember {
        ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(maxSize) { index ->
            val name = placeName.getOrNull(index)?.takeIf { it.isNotBlank() } ?: return@items
            val id = placeId.getOrNull(index)?.takeIf { it.isNotBlank() } ?: return@items
            val imageUrl = photoUrlList.getOrNull(index)?.takeIf { it.isNotBlank() }
                ?: "https://images.unsplash.com/photo-1583578568273-a1a95c9a5b23?q=80&w=1976&auto=format&fit=crop"
            val address = placeAddress.getOrNull(index)?.takeIf { it.isNotBlank() }
            val rating = placeRating.getOrNull(index)?.takeIf { it != 0.0 }?.toString()
            val totalRatings = placeTotalRatings.getOrNull(index)?.takeIf { it != 0 }?.toString()
            val description = descriptionList.getOrNull(index)?.takeIf { it.isNotBlank() }
            val extraInfo = extraInfoList.getOrNull(index)?.takeIf { it.isNotBlank() }

            Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                AllPlacesCard(
                    placeName = name,
                    photoUrl = imageUrl,
                    onClick = { onCardClick(id, name, address) },
                    address = address,
                    rating = rating,
                    totalRatings = totalRatings,
                    description = description,
                    extraInfo = extraInfo,
                    imageLoader = imageLoader
                )
            }
        }
    }
}

// Enhanced all places card with improved UI
@Composable
fun AllPlacesStarRating(
    rating: Float,
    maxStars: Int = 5,
    starSize: Dp = 16.dp,
    filledColor: Color = Color(0xFFFFC107), // Bright yellow color
    emptyColor: Color = Color.Gray.copy(alpha = 0.3f)
) {
    // Calculate number of full stars and whether to show half star
    val fullStars = rating.toInt()
    val hasHalfStar = rating % 1 != 0f // Check if there's a decimal part

    Row {
        repeat(maxStars) { index ->
            when {
                index < fullStars -> {
                    // Full star - for ratings like 3.2, show 3 full stars
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Full Star",
                        tint = filledColor,
                        modifier = Modifier.size(starSize)
                    )
                }
                index == fullStars && hasHalfStar -> {
                    // Half star - for ratings like 3.2, show half star as the 4th star
                    Box(modifier = Modifier.size(starSize)) {
                        // Empty star background
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = "Empty Star",
                            tint = emptyColor,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Half filled star using clip
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Half Star",
                            tint = filledColor,
                            modifier = Modifier
                                .fillMaxSize()
                                .clipToBounds()
                                .drawWithContent {
                                    clipRect(right = size.width * 0.5f) {
                                        this@drawWithContent.drawContent()
                                    }
                                }
                        )
                    }
                }
                else -> {
                    // Empty star
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "Empty Star",
                        tint = emptyColor,
                        modifier = Modifier.size(starSize)
                    )
                }
            }
        }
    }
}

@Composable
fun AllPlacesCard(
    placeName: String,
    photoUrl: String,
    onClick: () -> Unit,
    address: String? = null,
    rating: String? = null,
    totalRatings: String? = null,
    description: String? = null,
    extraInfo: String? = null,
    imageLoader: ImageLoader
) {
    var isFavorite by rememberSaveable { mutableStateOf(false) }

    // Hold detection
    var isHeld by remember { mutableStateOf(false) }

    // Animate scale based on hold state
    val scale by animateFloatAsState(
        targetValue = if (isHeld) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.9f, stiffness = 30f),
        label = "CardHoldScale"
    )

    // Parse rating as float for star calculation
    val ratingValue = rating?.toFloatOrNull() ?: 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            // Image section
            Row(Modifier) {
                Box(
                    modifier = Modifier
                        .height(150.dp)
                        .width(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    isHeld = true
                                },
                                onPress = {
                                    isHeld = true
                                    tryAwaitRelease()
                                    isHeld = false
                                },
                                onTap = {
                                    onClick()
                                }
                            )
                        }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(photoUrl)
                            .crossfade(true)
                            .placeholder(R.drawable.default_background_image1)
                            .error(R.drawable.default_background_image1)
                            .build(),
                        imageLoader = imageLoader,
                        contentDescription = "Place Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Subtle gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.1f)
                                    )
                                )
                            )
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isFavorite)
                                    Color.Red
                                else
                                    Color.White.copy(alpha = 0.95f)
                            )
                            .clickable { isFavorite = !isFavorite },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.White else Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Place name with better typography
                Text(
                    text = placeName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Address with location icon
                address?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Rating with custom star display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AllPlacesStarRating(rating = ratingValue, starSize = 16.dp)
                    Text(
                        text = String.format("%.1f", ratingValue),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                // Reviews with review icon
                if (totalRatings == null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.RateReview,
                            contentDescription = "Reviews",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "0 reviews",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
                totalRatings?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.RateReview,
                            contentDescription = "Reviews",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$it reviews",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                extraInfo?.let {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}