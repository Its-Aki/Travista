package com.example.travista.ui.screen

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavController) {
    val navItems = listOf(
        BottomNavItems.Home,
        BottomNavItems.Explore,
        BottomNavItems.Trips,
        BottomNavItems.Profile
    )
//    Text(
//        text = "Rating: $item4 ⭐",
//        modifier = Modifier.fillMaxWidth(),
//        fontSize = 14.sp
//    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar() {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) { // Prevents adding duplicate instances
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true // Ensures only one instance
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(imageVector = item.bottomNavIcons, contentDescription = null) },
                label = { Text(text = stringResource(id = item.label)) }
            )
        }
    }
}
