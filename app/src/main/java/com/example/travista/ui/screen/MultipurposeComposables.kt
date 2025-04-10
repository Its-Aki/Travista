package com.example.travista.ui.screen


import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import com.airbnb.lottie.compose.*
import kotlin.math.abs

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

    // 🧠 Use derivedStateOf to safely read layoutInfo
    val center by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
        }
    }

    val visibleItemIndices by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.map { it.index }
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
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

            val itemInfo by remember {
                derivedStateOf {
                    if (index in visibleItemIndices) {
                        listState.layoutInfo.visibleItemsInfo.find { it.index == index }
                    } else null
                }
            }


            val distanceFromCenter = itemInfo?.let {
                val itemCenter = it.offset + it.size / 2
                abs(center - itemCenter)
            } ?: 0

            val scaleFactor = animateFloatAsState(
                targetValue = if (distanceFromCenter < 200) 0.95f else 1f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = 200f),
                label = "Card Scale Animation"
            )

            Box(modifier = Modifier.scale(scaleFactor.value)) {
                PlaceCard(
                    placeName = name,
                    photoUrl = imageUrl,
                    onClick = { onCardClick(id, name, address) },
                    address = address,
                    rating = rating,
                    totalRatings = totalRatings,
                    description = description,
                    extraInfo = extraInfo,
                    isSingleItem = maxSize == 1
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
        isSingleItem: Boolean = false
    ) {
        val screenWidth =
            LocalContext.current.resources.displayMetrics.widthPixels / LocalContext.current.resources.displayMetrics.density
        var isFavorite by rememberSaveable { mutableStateOf(false) }

        // Hold detection
        var isHeld by remember { mutableStateOf(false) }

        // Animate scale based on hold state
        val scale by animateFloatAsState(
            targetValue = if (isHeld) 0.95f else 1f,
            animationSpec = spring(dampingRatio = 0.9f, stiffness = 30f),
            label = "CardHoldScale"
        )

        Column(
            modifier = if (isSingleItem) Modifier
                .width(screenWidth.dp)
                .padding(end = 4.dp) else Modifier.width(180.dp)
        ) {
            Box(
                modifier = Modifier
                    .scale(scale)
                    .height(250.dp)
                    .clip(RoundedCornerShape(20.dp))
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

            Text(
                text = placeName,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )

            address?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
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



