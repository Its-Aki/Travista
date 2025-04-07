package com.example.travista.ui.navigation.tabnavigation


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.travista.ui.screen.TripsScreen

@Composable
fun TripsNavGraph() {
    val tripsNavController = rememberNavController()
    Box(
        modifier = Modifier
            .padding(WindowInsets.systemBars.asPaddingValues()) // ✅ fix for camera cutout/status bar
            .fillMaxSize()
    ) {
        NavHost(
            navController = tripsNavController,
            startDestination = ScreenNavigation.MainTrips.route
        )
        {
            composable(route = ScreenNavigation.MainTrips.route) {
                TripsScreen(tripsNavController)
            }


        }

    }
}