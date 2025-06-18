package com.example.travista.data.tripsteps



data class TravelSteps(
    val route: List<TravelStep> = emptyList() // Default empty list if no route is returned
)

data class TravelStep(
    val stepIndex: Int = 0,
    val title: String = "",
    val travelMode: String = "",
    val distance: String = "",
    val duration: String = "",
    val to: String = "",
    val summary: String = "",
    val guides: String = ""
)
