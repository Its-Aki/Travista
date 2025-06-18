package com.example.travista.navigation

import android.net.Uri

sealed class ScreenNavigation(val route: String){
    //for auth route
    object AuthRoute: ScreenNavigation("authentication_route")
    //for login screen
    object LoginScreen: ScreenNavigation("login_screen")
    //for register screen
    object RegisterScreen: ScreenNavigation("register_screen")
    //for bottom nav graph
    object MainScreen: ScreenNavigation("main_screen")
    //     for home screen
    object HomeScreen: ScreenNavigation("home_Screen")
    // for explore screen
    object MainExplore: ScreenNavigation("main_explore")
    // for destination screen
    object Destination:ScreenNavigation("destination") {
        fun passArgs(placeName: String, address: String,placeId: String): String {
            return "$route/${Uri.encode(placeName)}/${Uri.encode(address)}/${Uri.encode(placeId)}"
        }
    }
    // for AllPlacesScreen
    object AllPlaces: ScreenNavigation("all_places"){
        fun passArgs(placeName: String, address: String,type: String): String {
            return "$route/${Uri.encode(placeName)}/${Uri.encode(address)}/${Uri.encode(type)}"
        }
    }

    // for destination full details screen
    object DestinationFullDetails: ScreenNavigation("destination_full_details"){
        fun passArgs(placeId: String): String {
            return "$route/${Uri.encode(placeId)}"
        }
    }
    // for nearby-locations screen in directed from home screen
    object NearbyPlaces:ScreenNavigation("nearby_places") {
        fun passArgs(placeType: String): String {
            return "$route/${Uri.encode(placeType)}"
        }
    }

    // for trips screen
    object MainTrips: ScreenNavigation("main_trips")
    // for trips input screen
    object TripsInput: ScreenNavigation("trips_input")
    // for trips display screen
    object TripsDisplay: ScreenNavigation("trips_display"){
        fun passArgs(startingAddress: String, destinationAddress: String): String {
            return "$route/${Uri.encode(startingAddress)}/${Uri.encode(destinationAddress)}"
        }
    }
    // for profile screen
    object MainProfile: ScreenNavigation("main_profile")
    object TravelPreferences: ScreenNavigation("travel_preferences")

}