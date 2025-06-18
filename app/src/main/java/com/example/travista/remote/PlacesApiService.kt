// com.example.travista.remote

package com.example.travista.remote


import com.example.travista.data.detailsofdestinaton.DetailsOFDestination
import com.example.travista.data.detinationfulldetails.DestinationFullDetails
import com.example.travista.data.model.AutocompleteResponse
import com.example.travista.data.nearbylocations.NearbyLocations
import com.example.travista.data.topdestionations.TopDestinations
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApiService {

    @GET("details/json")
    suspend fun getDestinationFullDetails(
        @Query("place_id") placeId: String,
        @Query("key") apiKey: String
    ): Response<DestinationFullDetailsResponse>

    @GET("textsearch/json")
    suspend fun getTopDestinations(
        @Query("query") query: String,
        @Query("key") apiKey: String,
        @Query("rankby") rankBy: String = "prominence"
    ): Response<TopDestinationsResponse>

    @GET("textsearch/json")
    suspend fun getDestinationDetails(
        @Query("query") query: String,
        @Query("key") apiKey: String,
        @Query("pagetoken") pageToken: String? = null
    ): Response<DetailsOfDestinationResponse>

    @GET("nearbysearch/json")
    suspend fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String,
        @Query("key") apiKey: String
    ): Response<NearbyPlacesResponse>

        @GET("autocomplete/json")
        suspend fun getPlaceSuggestions(
            @Query("input") input: String,
            @Query("key") apiKey: String,
            @Query("types") types: String = "(regions)"
        ): AutocompleteResponse
}


// Response wrappers

data class DestinationFullDetailsResponse(
    @SerializedName("result")
    val destinationFullDetails: DestinationFullDetails?
)

data class DetailsOfDestinationResponse(
    @SerializedName("results")
    val detailsOfDestination: List<DetailsOFDestination>?,
    @SerializedName("next_page_token")
    val nextPageToken: String?
)

data class TopDestinationsResponse(
    @SerializedName("results")
    val topDestinations: List<TopDestinations>?

)

data class NearbyPlacesResponse(
    @SerializedName("results")
    val nearbyLocations: List<NearbyLocations>?
)
