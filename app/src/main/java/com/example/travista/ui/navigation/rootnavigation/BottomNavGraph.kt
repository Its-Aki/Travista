package com.example.travista.ui.navigation.rootnavigation


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

@Composable
fun BottomNavGraph(rootNavController: NavHostController, stateHolder: SaveableStateHolder, modifier: Modifier) {
    NavHost(
        navController = rootNavController,
        startDestination = BottomNavItems.Home.route
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
