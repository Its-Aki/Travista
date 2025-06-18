package com.example.travista.navigation.rootnavigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.travista.navigation.ScreenNavigation
import com.example.travista.screens.login.AuthViewModel
import com.example.travista.screens.login.LoginScreen
import com.example.travista.screens.login.RegisterScreen

fun NavGraphBuilder.authNestedGraph(rootNavController: NavController,authViewModel: AuthViewModel){
    navigation(
        startDestination = ScreenNavigation.LoginScreen.route,
        route = ScreenNavigation.AuthRoute.route
    ) {
    composable(ScreenNavigation.LoginScreen.route) {
        LoginScreen(
            rootNavController = rootNavController,
            authViewModel = authViewModel,
            onLoginSuccess = {
                rootNavController.navigate(ScreenNavigation.MainScreen.route) {
                    popUpTo(ScreenNavigation.LoginScreen.route) {
                        inclusive = true
                    } // Clear login from back stack
                    launchSingleTop = true
                }
            },
            onNavigateToRegister = {
                rootNavController.navigate(ScreenNavigation.RegisterScreen.route)
            }
        )
    }
    composable(ScreenNavigation.RegisterScreen.route) {
        RegisterScreen(
            authViewModel = authViewModel,
            onNavigateToLogin = {
                rootNavController.navigate(ScreenNavigation.LoginScreen.route)
            },
            onRegisterSuccess = {
                rootNavController.navigate(ScreenNavigation.MainScreen.route) {
                    popUpTo(ScreenNavigation.RegisterScreen.route) {
                        inclusive = true
                    } // Clear register from back stack
                    launchSingleTop = true

                }
            }
        )
    }
}
}