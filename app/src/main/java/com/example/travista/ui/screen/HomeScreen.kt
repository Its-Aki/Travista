package com.example.travista.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travista.viewmodel.topdestinations.TopDestinationsViewModel
import com.example.travista.viewmodel.topdestinations.TopDestinationsViewModel.TopDestinationsUiState

@Composable
fun HomeScreen(
    viewModel: TopDestinationsViewModel = hiltViewModel(),
    onPlaceClick: (placeId: String, name: String, address: String?) -> Unit = { _, _, _ -> },
) {
    Column {
        HomeScreenPreview()
        val uiState by viewModel.topDestinationsState.collectAsState()

        when (uiState) {
            is TopDestinationsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is TopDestinationsUiState.Error -> {
                val message = (uiState as TopDestinationsUiState.Error).message
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Error: $message")
                }
            }

            is TopDestinationsUiState.Success -> {
                val destinations = (uiState as TopDestinationsUiState.Success).data

                if (destinations.isNotEmpty()) {
                    PlacesCarousel(
                        placeName = destinations.map { it.name },
                        placeId = destinations.map { it.placeId },
                        photoUrlList = destinations.map { it.photoUrl },
                        placeAddress = destinations.map { it.address },
                        placeRating = List(destinations.size) { null }, // Optional
                        placeTotalRatings = List(destinations.size) { null }, // Optional
                        descriptionList = List(destinations.size) { null }, // Optional
                        extraInfoList = List(destinations.size) { null }, // Optional
                    onCardClick = onPlaceClick
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No destinations available.")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true, backgroundColor=0xFFA0EAF2)
@Composable
fun HomeScreenPreview() {
    Column(
        modifier = Modifier.padding(top = 32.dp)
    ) {
    Row(
        modifier = Modifier
            .padding(start=16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Let's Go ",
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
            fontSize = 28.sp,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = {}
        ) {
                Icon(
                    imageVector = Icons.Filled.NotificationsNone,
                    contentDescription = "Notification",
                )
        }
        //
    }
        OutlinedButton(
            onClick ={
            },
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            enabled = true,
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            elevation = ButtonDefaults.buttonElevation(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        ) {
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start){
                Icon(Icons.Filled.Search,
                    contentDescription = "Search")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(text = "Places to go,things to do,hotels...")
            }

        }
    }
}