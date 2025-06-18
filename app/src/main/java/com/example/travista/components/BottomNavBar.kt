package com.example.travista.components


import android.R.attr.label
import android.R.id.home
import android.net.http.SslCertificate.restoreState
import android.net.http.SslCertificate.saveState
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import com.example.travista.navigation.rootnavigation.BottomNavItems
import com.google.android.material.bottomappbar.BottomAppBar

@Composable
fun BottomNavBar(bottomNavController: NavController) {
    val navItems = listOf(
        BottomNavItems.Home,
        BottomNavItems.Explore,
        BottomNavItems.Trips,
        BottomNavItems.Profile
    )


    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomBarNavOption = navOptions {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(bottomNavController.graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
    BackHandler(enabled = currentRoute != BottomNavItems.Home.route) {
        bottomNavController.popBackStack()
    }

    NavigationBar (
        modifier = Modifier
    ) {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    bottomNavController.navigate(item.route, navOptions = bottomBarNavOption)
                },
                icon = { Icon(imageVector = item.bottomNavIcons, contentDescription = null) },
                label = { Text(text = stringResource(id = item.label)) }
            )
        }
    }
}
