package com.example.travista.screens.trip

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.travista.theme.TravistaTheme


@Composable
fun TripsScreen(navController: NavController) {
    TwoSearchBar(
        navController = navController
    )
         // TripsData()


       //TravelStepsScreen(startingAddress = "Kashmir, Himachal Pradesh, India", destinationAddress = "Kanyakumari, Tamil Nadu, India")



    }




@Preview(showBackground = true)
@Composable
fun TripsScreenPreview() {
    TravistaTheme {

    }
}
