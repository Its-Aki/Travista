package com.example.travista.data.topdestionations

import com.example.travista.data.getApikey
import com.google.gson.annotations.SerializedName

data class TopDestinations(
    @SerializedName("name")
    val name: String? = "Unknown",

    @SerializedName("place_id")
    val placeId: String? = null,

    @SerializedName("formatted_address")
    val address: String? = "Not Available",

    @SerializedName("photos")
    val photos: List<Photo> = emptyList()
) {
    val photoUrl: String
        get() = photos.firstOrNull()?.photoReference?.let { ref ->
            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$ref&key=${getApikey()}"
        } ?: "https://images.unsplash.com/photo-1583578568273-a1a95c9a5b23?q=80&w=1976&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
}

data class Photo(
    @SerializedName("photo_reference")
    val photoReference: String?
)
