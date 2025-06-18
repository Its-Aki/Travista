package com.example.travista.repository.detailsofdestination

import android.util.Log
import com.example.travista.data.getApikey
import com.example.travista.data.tripsteps.Content
import com.example.travista.data.tripsteps.GeminiRequest
import com.example.travista.data.tripsteps.Part
import com.example.travista.remote.DetailsOfDestinationResponse
import com.example.travista.remote.GeminiApiService
import com.example.travista.remote.PlacesApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PlacesRepository @Inject constructor(
    private val apiService: PlacesApiService,
    private val geminiApiService: GeminiApiService
) {
    suspend fun fetchTopHotels(placeName: String, placeAddress: String,pageToken: String? = null): Result<DetailsOfDestinationResponse> {
        return fetchPlaces("top hotels near $placeName, $placeAddress",pageToken)
    }

    suspend fun fetchTopRestaurants(placeName: String, placeAddress: String,pageToken: String? = null): Result<DetailsOfDestinationResponse> {
        return fetchPlaces("top restaurants near $placeName, $placeAddress",pageToken)
    }

    suspend fun fetchTopAttractions(placeName: String, placeAddress: String,pageToken: String? = null): Result<DetailsOfDestinationResponse> {
        return fetchPlaces("top things to do near $placeName, $placeAddress",pageToken)
    }

    suspend fun fetchPlaceSummary(prompt: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val request = GeminiRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(Part(text = prompt))
                        )
                    )
                )

                val apiKey = getApikey()
                Log.d("GeminiApi", "Sending Gemini request ") // Verify key is passed
                val response = geminiApiService.getTravelPlan(apiKey, request)

                if (response?.isSuccessful ?: false) {
                    val summary = response?.body()
                        ?.candidates?.firstOrNull()
                        ?.content?.parts?.firstOrNull()
                        ?.text
                    Log.d("GeminiApi", "Summary received: $summary")
                    return@withContext Result.success(summary ?: "")
                } else {
                    val errorBody = response?.errorBody()?.string()
                    val errorCode = response?.code()
                    Log.e("GeminiApi", "Gemini API failed with code $errorCode: $errorBody")
                    // Propagate the actual error
                    return@withContext Result.failure(HttpException(response))
                }
            } catch (e: IOException) {
                // Network error (no internet, host unreachable, etc.)
                Log.e("GeminiApi", "IOException during Gemini API call: ${e.message}", e)
                return@withContext Result.failure(e)
            } catch (e: HttpException) {
                // HTTP error (4xx, 5xx responses)
                Log.e("GeminiApi", "HttpException during Gemini API call: ${e.code()} - ${e.message()}", e)
                return@withContext Result.failure(e)
            } catch (e: Exception) {
                // Any other unexpected error
                Log.e("GeminiApi", "Generic Exception during Gemini API call: ${e.message}", e)
                return@withContext Result.failure(e)
            }
        }
    }

    private suspend fun fetchPlaces(query: String,pageToken: String? = null): Result<DetailsOfDestinationResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val apiKey = getApikey()
                val response = apiService.getDestinationDetails(query, apiKey,pageToken) // Using same API
                //  Log.d("delete", response.toString())
                if (response.isSuccessful) {
                    val body = response.body()
                    val placesList = body?.detailsOfDestination.orEmpty()
                    val nextToken = body?.nextPageToken

                    return@withContext if (body != null && placesList.isNotEmpty()) {
                        Result.success(DetailsOfDestinationResponse(placesList,nextToken))
                    } else {
                        Result.failure(Exception("No results found for: $query"))
                    }
                }

                val errorMessage = response.errorBody()?.string() ?: response.message()
                return@withContext Result.failure(Exception("API request failed: $errorMessage"))

            } catch (e: IOException) {
                return@withContext Result.failure(Exception("No internet connection. Please check your network."))
            } catch (e: HttpException) {
                return@withContext Result.failure(Exception("API error: ${e.response()?.errorBody()?.string() ?: e.message()}"))
            } catch (e: Exception) {
                return@withContext Result.failure(Exception("Something went wrong: ${e.localizedMessage}"))
            }
        }
    }
}
//
//// list of hotels, restaurants and attraction
//private val _topHotelData = MutableStateFlow<Result<List<DetailsOFDestination>>>(Result.success(emptyList()))
//val topHotelData: StateFlow<Result<List<DetailsOFDestination>>> = _topHotelData
//
//private val _topRestaurantData = MutableStateFlow<Result<List<DetailsOFDestination>>>(Result.success(emptyList()))
//val topRestaurantData: StateFlow<Result<List<DetailsOFDestination>>> = _topRestaurantData
//
//private val _topAttractionsData = MutableStateFlow<Result<List<DetailsOFDestination>>>(Result.success(emptyList()))
//val topAttractionsData: StateFlow<Result<List<DetailsOFDestination>>> = _topAttractionsData
//
//// tokens
//private val _hotelNextToken = MutableStateFlow<String?>(null)
//val hotelNextToken: StateFlow<String?> = _hotelNextToken
//
//private val _restaurantNextToken = MutableStateFlow<String?>(null)
//val restaurantNextToken: StateFlow<String?> = _restaurantNextToken
//
//private val _attractionNextToken = MutableStateFlow<String?>(null)
//val attractionNextToken: StateFlow<String?> = _attractionNextToken

// repository viewmodel

//package com.example.travista.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.travista.data.detailsofdestinaton.DetailsOFDestination
//import com.example.travista.repository.detailsofdestination.PlacesRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class DestinationViewModel @Inject constructor(
//    private val placesRepository: PlacesRepository
//) : ViewModel() {
//
//    private val _topHotelData = MutableStateFlow<Result<List<DetailsOFDestination>>>(Result.success(emptyList()))
//    val topHotelData: StateFlow<Result<List<DetailsOFDestination>>> = _topHotelData
//
//    private val _topRestaurantData = MutableStateFlow<Result<List<DetailsOFDestination>>>(Result.success(emptyList()))
//    val topRestaurantData: StateFlow<Result<List<DetailsOFDestination>>> = _topRestaurantData
//
//    private val _topAttractionsData = MutableStateFlow<Result<List<DetailsOFDestination>>>(Result.success(emptyList()))
//    val topAttractionsData: StateFlow<Result<List<DetailsOFDestination>>> = _topAttractionsData
//
//
//
//
//    private var hasFetchedData = false
//
//    fun fetchDestinationData(placeName: String, placeAddress: String) {
//        if (hasFetchedData) return
//
//        viewModelScope.launch {
//
//            _topHotelData.value = placesRepository.fetchTopHotels(placeName, placeAddress)
//            _topRestaurantData.value = placesRepository.fetchTopRestaurants(placeName, placeAddress)
//            _topAttractionsData.value = placesRepository.fetchTopAttractions(placeName, placeAddress)
//
//
//
//            hasFetchedData = true
//
//        }
//    }
//}
