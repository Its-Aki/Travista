package com.example.travista.ui.navigation.rootnavigation


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.travista.ui.navigation.tabnavigation.ExploreNavGraph
import com.example.travista.ui.navigation.tabnavigation.HomeNavGraph
import com.example.travista.ui.navigation.tabnavigation.ProfileNavGraph
import com.example.travista.ui.navigation.tabnavigation.TripsNavGraph
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomNavGraph(rootNavController: NavHostController, stateHolder: SaveableStateHolder, modifier: Modifier) {
    AnimatedNavHost(
        navController = rootNavController,
        startDestination = BottomNavItems.Home.route,
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

    ) {
        composable(BottomNavItems.Home.route) {
            stateHolder.SaveableStateProvider(BottomNavItems.Home.route) {
                HomeNavGraph(rootNavController)
            }
        }
        composable(BottomNavItems.Explore.route) {
            ExploreNavGraph()
        }
        composable(BottomNavItems.Trips.route) {
            TripsNavGraph()
        }
        composable(BottomNavItems.Profile.route) {
            ProfileNavGraph() }
    }
}
