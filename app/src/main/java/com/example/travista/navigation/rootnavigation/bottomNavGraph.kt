package com.example.travista.navigation.rootnavigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.travista.navigation.ScreenNavigation
import com.example.travista.navigation.tabnavigation.ExploreNavGraph
import com.example.travista.navigation.tabnavigation.HomeNavGraph
import com.example.travista.navigation.tabnavigation.ProfileNavGraph
import com.example.travista.navigation.tabnavigation.TripsNavGraph
import com.example.travista.screens.login.AuthViewModel

fun NavGraphBuilder.bottomNavGraph(rootNavController: NavController,authViewModel: AuthViewModel){
    //nested bottom nav graph
    navigation(
        startDestination = BottomNavItems.Home.route,
        route = ScreenNavigation.MainScreen.route
    ) {
        composable(BottomNavItems.Home.route) {
            HomeNavGraph(rootNavController)
        }
        composable(BottomNavItems.Explore.route) {
            ExploreNavGraph()
        }
        composable(BottomNavItems.Trips.route) {
            TripsNavGraph()
        }
        composable(BottomNavItems.Profile.route) {
            ProfileNavGraph(rootNavController,authViewModel)
        }
    }
}