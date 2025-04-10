package com.example.travista.ui.navigation.tabnavigation

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.travista.ui.screen.ProfileScreen
import com.example.travista.ui.screen.TripsScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfileNavGraph() {
    val profileNavController = rememberNavController()
    Box(
        modifier = Modifier
            .padding(WindowInsets.systemBars.asPaddingValues()) // ✅ fix for camera cutout/status bar
            .fillMaxSize()
    ) {
        AnimatedNavHost(
            navController = profileNavController,
            startDestination = ScreenNavigation.MainProfile.route,
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
            composable(route = ScreenNavigation.MainProfile.route) {
                ProfileScreen(profileNavController)
            }


        }

    }
}