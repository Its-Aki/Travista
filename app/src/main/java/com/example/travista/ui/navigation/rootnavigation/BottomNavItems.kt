package com.example.travista.ui.navigation.rootnavigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.travista.R

sealed class BottomNavItems(
    val route: String,
    val bottomNavIcons: ImageVector,
    val label: Int
){

    object Home: BottomNavItems("home", Icons.Outlined.Home, R.string.ic_home_label)
    object Explore: BottomNavItems("explore", Icons.Outlined.Search, R.string.ic_explore_label)
    object Trips: BottomNavItems("trips", Icons.Outlined.Luggage, R.string.ic_trips_label)
    object Profile: BottomNavItems("profile", Icons.Outlined.Person, R.string.ic_profile_label)


}