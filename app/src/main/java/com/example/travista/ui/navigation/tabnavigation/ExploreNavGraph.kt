package com.example.travista.ui.navigation.tabnavigation

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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.travista.ui.screen.DestinationDescription
import com.example.travista.ui.screen.DestinationFullDetailsScreen
import com.example.travista.ui.screen.ExploreScreen
import com.example.travista.ui.screen.SearchScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExploreNavGraph() {

    val exploreNavController = rememberNavController()
    Box(
        modifier = Modifier
            .padding(WindowInsets.systemBars.asPaddingValues()) // ✅ fix for camera cutout/status bar
            .fillMaxSize()
    ) {
        AnimatedNavHost(
            navController = exploreNavController,
            startDestination = ScreenNavigation.MainExplore.route,
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
            composable(route = ScreenNavigation.MainExplore.route) {
                SearchScreen(navController = exploreNavController)
            }
            composable(
                route = ScreenNavigation.Destination.route + "/{placeName}/{address}/{placeId}",
                arguments = listOf(
                    navArgument("placeName") { type = NavType.StringType },
                    navArgument("address") { type = NavType.StringType },
                    navArgument("placeId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val placeName = Uri.decode(backStackEntry.arguments?.getString("placeName") ?: "")
                val address = Uri.decode(backStackEntry.arguments?.getString("address") ?: "")
                val placeId = Uri.decode(backStackEntry.arguments?.getString("placeId") ?: "")
                DestinationDescription(placeName, address, placeId, exploreNavController)
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