package com.example.travista.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.travista.R
import com.example.travista.ui.navigation.rootnavigation.BottomNavItems
import com.example.travista.ui.navigation.tabnavigation.ScreenNavigation
import com.example.travista.viewmodel.topdestinations.TopDestinationsViewModel
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
                            PlacesCarousel(
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
                .error(R.drawable.background)
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
