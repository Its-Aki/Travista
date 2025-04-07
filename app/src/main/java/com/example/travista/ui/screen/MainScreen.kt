package com.example.travista.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.travista.ui.navigation.rootnavigation.BottomNavBar
import com.example.travista.ui.navigation.rootnavigation.BottomNavGraph
import com.example.travista.ui.navigation.rootnavigation.BottomNavItems

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val stateHolder = rememberSaveableStateHolder()

    // Get the current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Handle back press: Enable only when NOT on Home
    BackHandler(enabled = currentRoute != BottomNavItems.Home.route) {
        navController.popBackStack()
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        BottomNavGraph(
            rootNavController = navController,
            stateHolder = stateHolder,
            modifier = Modifier.padding(paddingValues)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {

    MainScreen()
}
