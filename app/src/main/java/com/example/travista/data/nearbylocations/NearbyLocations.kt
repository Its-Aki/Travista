package com.example.travista.data.nearbylocations

import com.example.travista.data.detinationfulldetails.Geometry
import com.example.travista.data.detinationfulldetails.Photo
import com.example.travista.data.getApikey
import com.google.gson.annotations.SerializedName

data class NearbyLocations(
    @SerializedName("name")
    val name: String? = "Unknown ",

    @SerializedName("place_id")
    val placeId: String? = null,

    @SerializedName("rating")
    val rating: Double? = 0.0,

    @SerializedName("user_ratings_total")
    val totalRatings: Int? = 0,

    @SerializedName("vicinity")
    val vicinity: String? = "Location not available",

    @SerializedName("types")
    val types: List<String>? = emptyList(),

    @SerializedName("geometry")
    val geometry: Geometry? = null,

    @SerializedName("photos")
    val photos: List<Photo> = emptyList()
) {
    fun getDestinationPhotoUrl(apiKey: String = getApikey()): String {
        return photos.firstOrNull()?.photoReference?.let { ref ->
            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$ref&key=$apiKey"
        } ?: "https://images.unsplash.com/photo-1726044781946-fca0baa0cdcd?q=80&w=715&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
    }
}

data class Geometry(
    @SerializedName("location")
    val location: LatLng?
)

data class LatLng(
    @SerializedName("lat")
    val lat: Double? = 0.0,

    @SerializedName("lng")
    val lng: Double?= 0.0
)

data class Photo(
    @SerializedName("photo_reference")
    val photoReference: String?
)

