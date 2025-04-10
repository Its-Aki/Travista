package com.example.travista.data.detailsofdestinaton



import com.example.travista.data.getApikey
import com.google.gson.annotations.SerializedName

data class DetailsOFDestination(
    @SerializedName("name")
    val name: String? = "Unknown ",

    @SerializedName("place_id")
    val placeId: String? = null,

    @SerializedName("rating")
    val rating: Double? = 0.0,  // Default rating to 0.0


    @SerializedName("user_ratings_total")
    val totalRatings: Int? = 0,  // Default total ratings to 0

    @SerializedName("photos")
    val photos: List<Photo> = emptyList()  // Nullable list to handle missing images
) {
    // Get photo URL (if available), otherwise return default image

    fun getDestinationPhotoUrl(apiKey: String= getApikey()): String {
        return photos.firstOrNull()?.photoReference?.let { ref ->
            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$ref&key=$apiKey"
        } ?: "https://images.unsplash.com/photo-1583578568273-a1a95c9a5b23?q=80&w=1976&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" // Fallback image
    }
}

// Photo class to handle the "photos" array
data class Photo(
    @SerializedName("photo_reference")
    val photoReference: String?
)
