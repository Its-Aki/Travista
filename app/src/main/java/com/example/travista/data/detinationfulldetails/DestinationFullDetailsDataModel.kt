package com.example.travista.data.detinationfulldetails

import com.google.gson.annotations.SerializedName

data class DestinationFullDetails(
    @SerializedName("name") val name: String? = "Not Available",
    @SerializedName("formatted_address") val address: String? = "Not Available",
    @SerializedName("formatted_phone_number") val phoneNumber: String? = "Not Available",
    @SerializedName("website") val website: String? = "Not Available",
    @SerializedName("url") val googleMapsUrl: String? = "Not Available",
    @SerializedName("rating") val rating: Double? = 0.0,
    @SerializedName("user_ratings_total") val totalRatings: Int? = 0,
    @SerializedName("price_level") val priceLevel: Int? = -1,
    @SerializedName("opening_hours") val openingHours: OpeningHours? = null,
    @SerializedName("reviews") val reviews: List<Review>? = emptyList(),
    @SerializedName("photos") val photos: List<Photo>? = emptyList(),
    @SerializedName("geometry") val geometry: Geometry? = null,
    @SerializedName("types") val types: List<String>? = listOf("Not Available")
)

// ✅ Location Details (Latitude & Longitude)
data class Geometry(
    @SerializedName("location") val location: Location? = null // ✅ Fully nullable to avoid crashes
)

data class Location(
    @SerializedName("lat") val latitude: Double? = 0.0,
    @SerializedName("lng") val longitude: Double? = 0.0
)

// ✅ Opening Hours
data class OpeningHours(
    @SerializedName("open_now") val isOpenNow: Boolean? = false,
    @SerializedName("weekday_text") val weekdayText: List<String>? = listOf("Not Available")
)

// ✅ User Reviews
data class Review(
    @SerializedName("author_name") val authorName: String? = "Anonymous",
    @SerializedName("profile_photo_url") val profileImageUrl: String? = null,
    @SerializedName("rating") val rating: Double? = 0.0,
    @SerializedName("text") val reviewText: String? = "No Review"
)

// ✅ Hotel Photos
data class Photo(
    @SerializedName("photo_reference") val photoReference: String? = null
)

// ✅ Helper Function to Get Price Level as Text
fun getPriceLevelText(priceLevel: Int?): String {
    return when (priceLevel) {
        0 -> "Free"
        1 -> "Inexpensive"
        2 -> "Moderate"
        3 -> "Expensive"
        4 -> "Very Expensive"
        else -> "Not Available"
    }
}
