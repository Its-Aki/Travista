package com.example.travista.ui.navigation.tabnavigation

import android.net.Uri

sealed class ScreenNavigation(val route: String){
    object MainHome: ScreenNavigation("main_home")
    object MainExplore: ScreenNavigation("main_explore")
    object Destination:ScreenNavigation("destination") {
        fun passArgs(placeName: String, address: String): String {
            return "$route/${Uri.encode(placeName)}/${Uri.encode(address)}"
        }
    }
    object DestinationFullDetails: ScreenNavigation("destination_full_details"){
        fun passArgs(placeId: String): String {
            return "$route/${Uri.encode(placeId)}"
        }
    }


    object MainTrips: ScreenNavigation("main_trips")
    object MainProfile: ScreenNavigation("main_profile")

}