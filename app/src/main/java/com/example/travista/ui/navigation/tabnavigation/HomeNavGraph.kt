package com.example.travista.ui.navigation.tabnavigation

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.travista.ui.screen.DestinationDescription
import com.example.travista.ui.screen.DestinationFullDetailsScreen
import com.example.travista.ui.screen.HomeScreen

@Composable
fun HomeNavGraph(rootNavController: NavController) {
    val homeNavController = rememberNavController()

    Box(
        modifier = Modifier
            .padding(WindowInsets.systemBars.asPaddingValues()) // ✅ fix for camera cutout/status bar
            .fillMaxSize()
    ) {
        NavHost(
            navController = homeNavController,
            startDestination = ScreenNavigation.MainHome.route
        ) {
            composable(route = ScreenNavigation.MainHome.route) {
                HomeScreen(homeNavController,rootNavController)
            }

            composable(
                route = ScreenNavigation.Destination.route + "/{placeName}/{address}",
                arguments = listOf(
                    navArgument("placeName") { type = NavType.StringType },
                    navArgument("address") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val placeName = Uri.decode(backStackEntry.arguments?.getString("placeName") ?: "")
                val address = Uri.decode(backStackEntry.arguments?.getString("address") ?: "")
                DestinationDescription(placeName, address,homeNavController)
            }
            composable(route = ScreenNavigation.DestinationFullDetails.route + "/{placeId}" ,
                arguments = listOf(
                    navArgument("placeId") { type = NavType.StringType }
                )
            )
             { backStackEntry ->
                val placeId = Uri.decode(backStackEntry.arguments?.getString("placeId") ?: "")
                 DestinationFullDetailsScreen(placeId)



            }
        }
    }
}
