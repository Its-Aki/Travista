package com.example.travista.navigation


import android.R.attr.bottom
import android.R.attr.padding
import android.content.Context
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import com.example.travista.components.BottomNavBar
import com.example.travista.navigation.rootnavigation.BottomNavItems
import com.example.travista.navigation.rootnavigation.authNestedGraph
import com.example.travista.navigation.rootnavigation.bottomNavGraph
import com.example.travista.navigation.tabnavigation.ExploreNavGraph
import com.example.travista.navigation.tabnavigation.HomeNavGraph
import com.example.travista.navigation.tabnavigation.ProfileNavGraph
import com.example.travista.navigation.tabnavigation.TripsNavGraph
import com.example.travista.screens.login.AuthViewModel
import com.example.travista.screens.login.LoginScreen
import com.example.travista.screens.login.RegisterScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(rootNavController: NavHostController,context: Context) {
    val authViewModel: AuthViewModel = viewModel() // Shared ViewModel
    val firebaseAuth = FirebaseAuth.getInstance()
    val isUserLoggedIn = firebaseAuth.currentUser != null
    val startDestination = if (isUserLoggedIn) ScreenNavigation.MainScreen.route else ScreenNavigation.AuthRoute.route
    val navBackStackEntry by rootNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val navItems = listOf(
        BottomNavItems.Home,
        BottomNavItems.Explore,
        BottomNavItems.Trips,
        BottomNavItems.Profile
    )

    val showBottomBar= currentRoute in navItems.map { it.route }
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(rootNavController)
            }
        }
    ) { padding ->
        NavHost(rootNavController, startDestination = startDestination) {
            authNestedGraph(rootNavController,authViewModel)
            bottomNavGraph(rootNavController,authViewModel)
        }
    }
}
