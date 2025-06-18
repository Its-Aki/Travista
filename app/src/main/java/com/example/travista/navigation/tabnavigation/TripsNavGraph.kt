package com.example.travista.navigation.tabnavigation


import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.travista.navigation.ScreenNavigation
import com.example.travista.screens.explore.AllPlacesScreen
import com.example.travista.screens.home.DestinationFullDetailsScreen
import com.example.travista.screens.trip.TravelStepsScreen
import com.example.travista.screens.trip.TripsScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TripsNavGraph() {
    val tripsNavController = rememberNavController()
    Box(
        modifier = Modifier
            .padding(WindowInsets.systemBars.asPaddingValues()) //  fix for camera cutout/status bar
            .fillMaxSize()
    ) {
        AnimatedNavHost(
            navController = tripsNavController,
            startDestination = ScreenNavigation.MainTrips.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut(animationSpec = tween(300))
            }
        )
        {
            composable(route = ScreenNavigation.MainTrips.route) {
                TripsScreen(tripsNavController)
            }
            composable(
                route = ScreenNavigation.TripsDisplay.route + "/{startingAddress}/{destinationAddress}",
                arguments = listOf(
                    navArgument("startingAddress") { type = NavType.StringType },
                    navArgument("destinationAddress") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val startingAddress = Uri.decode(backStackEntry.arguments?.getString("startingAddress") ?: "")
                val destinationAddress = Uri.decode(backStackEntry.arguments?.getString("destinationAddress") ?: "")

                TravelStepsScreen(startingAddress, destinationAddress,tripsNavController)
            }

            /////// - --------
            composable(route = ScreenNavigation.DestinationFullDetails.route + "/{placeId}" ,
                arguments = listOf(
                    navArgument("placeId") { type = NavType.StringType }
                )
            )
            { backStackEntry ->
                val placeId = Uri.decode(backStackEntry.arguments?.getString("placeId") ?: "")
                DestinationFullDetailsScreen(placeId)

            }

            // for all places screen
            composable(
                route = ScreenNavigation.AllPlaces.route + "/{placeName}/{address}/{type}",
                arguments = listOf(
                    navArgument("placeName") { type = NavType.StringType },
                    navArgument("address") { type = NavType.StringType },
                    navArgument("type") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val placeName = Uri.decode(backStackEntry.arguments?.getString("placeName") ?: "")
                val address = Uri.decode(backStackEntry.arguments?.getString("address") ?: "")
                val type = Uri.decode(backStackEntry.arguments?.getString("type") ?: "")
                AllPlacesScreen(tripsNavController,placeName,address,type)
            }
          //////////----------


        }

    }
}