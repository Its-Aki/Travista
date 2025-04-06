package com.example.travista.ui.screen


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun BottomNavGraph(bottomNavController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = bottomNavController,
        startDestination = BottomNavItems.Home.route,
        modifier = modifier
    ) {
        composable(BottomNavItems.Home.route) { HomeScreen() }
        composable(BottomNavItems.Explore.route) { ExploreScreen() }
        composable(BottomNavItems.Trips.route) { TripsScreen() }
        composable(BottomNavItems.Profile.route) { ProfileScreen() }
    }
}
