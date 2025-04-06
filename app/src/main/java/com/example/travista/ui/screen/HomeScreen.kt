package com.example.travista.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.travista.viewmodel.topdestinations.TopDestinationsViewModel
import kotlinx.coroutines.delay
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travista.R

@Composable
fun HomeScreen(
    onPlaceClick: (placeId: String, name: String, address: String?) -> Unit = { _, _, _ -> },
) {
    val viewModel: TopDestinationsViewModel = hiltViewModel()
    val scrollState = rememberLazyListState()
    val isScrolled = remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex > 0 || scrollState.firstVisibleItemScrollOffset > 395 // Increase offset to make search bar appear later
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = scrollState) {
            item {
                BackgroundWithSearch()
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Adding destination carousel
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
                            PlacesCarousel(
                                placeName = data.map { it.name },
                                placeId = data.map { it.placeId },
                                photoUrlList = data.map { it.photoUrl },
                                placeAddress = data.map { it.address },
                                placeRating = List(data.size) { null },
                                placeTotalRatings = List(data.size) { null },
                                descriptionList = List(data.size) { null },
                                extraInfoList = List(data.size) { null },
                                onCardClick = onPlaceClick
                            )
                        } else {
                            Text("No destinations available", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }

            items(15) {
                Text(
                    "Recommended Section $it",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )
            }
        }

        // for making Sticky Search Bar appear only after scrolls past a larger offset
        AnimatedVisibility(
            visible = isScrolled.value,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -80 }, animationSpec = tween(300)),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -80 }, animationSpec = tween(300))
        ) {
            StickySearchBar()
        }
    }
}

@Composable
fun BackgroundWithSearch() {
    var imageIndex by remember { mutableIntStateOf(0) }

    // Using Picsum random grayscale photo
    val imageUrl = "https://picsum.photos/800/600?grayscale&random=$imageIndex"

    LaunchedEffect(Unit) {
        while (true) {
            delay(6000)
            imageIndex++
        }
    }

    // Image placeholder
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)  // background height for the image placeholder
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .error(R.drawable.background) // Fallback to default image in case of error
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // For Gradient overlay
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
                modifier = Modifier
                    .padding(top = 50.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Plan your next adventure with us",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 1f)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            SearchBar(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    val isImagePresent = remember { mutableStateOf(true) } // Assume images are present initially
    val backgroundColor = if (isImagePresent.value) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
    val borderColor = if (isImagePresent.value) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)

    // No shimmer effect here, just a clickable SearchBar
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = modifier
            .height(60.dp) // match the Search bar height to match previous search bar design
            .fillMaxWidth()
            .clickable(
                enabled = true,
                indication = null, // No ripple effect
                interactionSource = remember { MutableInteractionSource() }, // Necessary when removing ripple
                onClick = {
                    // Handle click logic here
                    // For example, navigate to the search screen or trigger a search action
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
                modifier = Modifier.size(22.dp) // Keeping icon size consistent
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
fun StickySearchBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 1f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        SearchBar(modifier = Modifier.fillMaxWidth())
    }
}
