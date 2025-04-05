package com.example.travista.remote

import com.example.travista.data.detailsofdestinaton.DetailsOFDestination
import com.example.travista.data.detinationfulldetails.DestinationFullDetails
import com.example.travista.data.topdestionations.TopDestinations
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApiService {

    // To fetch full detailed details of a destination
    @GET("details/json")
    suspend fun getDestinationFullDetails(
        @Query("place_id") placeId: String,
        @Query("key") apiKey: String
    ): Response<DestinationFullDetailsResponse>

    // to fetch list of top destinations around the world
    @GET("textsearch/json")
    suspend fun getTopDestinations(
        @Query("query") query: String,
        @Query("key") apiKey: String
    ): Response<TopDestinationsResponse>

    // To fetch list of  top hotels, top restaurants, top attractions in a specified location
    @GET("textsearch/json")
    suspend fun getDestinationDetails(
        @Query("query") query: String,
        @Query("key") apiKey: String
    ): Response<DetailsOfDestinationResponse>

}




// For destination full details response
data class DestinationFullDetailsResponse(
    @SerializedName("result") val destinationFullDetails:DestinationFullDetails?
)

//  Fixed: Top hotels response now returns a **list** instead of a single object
data class DetailsOfDestinationResponse(
    @SerializedName("results") val detailsOfDestination: List<DetailsOFDestination>?
)

data class TopDestinationsResponse(
    @SerializedName("results") val topDestinations: List<TopDestinations>?
)
