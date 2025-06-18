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

/// repository backup temporary
//package com.example.travista.repository.detailsofdestination
//
//import com.example.travista.data.getApikey
//import com.example.travista.remote.DetailsOfDestinationResponse
//import com.example.travista.remote.PlacesApiService
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import retrofit2.HttpException
//import java.io.IOException
//import javax.inject.Inject
//
//class PlacesRepository @Inject constructor(
//    private val apiService: PlacesApiService
//) {
//    suspend fun fetchTopHotels(placeName: String, placeAddress: String,pageToken: String? = null): Result<DetailsOfDestinationResponse> {
//        return fetchPlaces("top hotels near $placeName, $placeAddress",pageToken)
//    }
//
//    suspend fun fetchTopRestaurants(placeName: String, placeAddress: String,pageToken: String? = null): Result<DetailsOfDestinationResponse> {
//        return fetchPlaces("top restaurants near $placeName, $placeAddress",pageToken)
//    }
//
//    suspend fun fetchTopAttractions(placeName: String, placeAddress: String,pageToken: String? = null): Result<DetailsOfDestinationResponse> {
//        return fetchPlaces("top things to do near $placeName, $placeAddress",pageToken)
//    }
//
//    private suspend fun fetchPlaces(query: String,pageToken: String? = null): Result<DetailsOfDestinationResponse> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val apiKey = getApikey()
//                val response = apiService.getDestinationDetails(query, apiKey,pageToken) // Using same API
//                // Log.d("delete", response.toString())
//                if (response.isSuccessful) {
//                    val body = response.body()
//                    val placesList = body?.detailsOfDestination.orEmpty()
//                    val nextToken = body?.nextPageToken
//
//                    return@withContext if (body != null && placesList.isNotEmpty()) {
//                        Result.success(DetailsOfDestinationResponse(placesList,nextToken))
//                    } else {
//                        Result.failure(Exception("No results found for: $query"))
//                    }
//                }
//
//                val errorMessage = response.errorBody()?.string() ?: response.message()
//                return@withContext Result.failure(Exception("API request failed: $errorMessage"))
//
//            } catch (e: IOException) {
//                return@withContext Result.failure(Exception("No internet connection. Please check your network."))
//            } catch (e: HttpException) {
//                return@withContext Result.failure(Exception("API error: ${e.response()?.errorBody()?.string() ?: e.message()}"))
//            } catch (e: Exception) {
//                return@withContext Result.failure(Exception("Something went wrong: ${e.localizedMessage}"))
//            }
//        }
//    }
//}


/// viewmodel backup temporary
//package com.example.travista.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.travista.data.detailsofdestinaton.DetailsOFDestination
//import com.example.travista.repository.detailsofdestination.PagedPlacesResult
//import com.example.travista.repository.detailsofdestination.PlacesRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class DestinationViewModel @Inject constructor(
//    private val repository: PlacesRepository
//) : ViewModel() {
//
//    // Flows to expose data for UI
//    private val _hotels = MutableStateFlow<List<DetailsOFDestination>>(emptyList())
//    val hotels: StateFlow<List<DetailsOFDestination>> = _hotels
//
//    private val _restaurants = MutableStateFlow<List<DetailsOFDestination>>(emptyList())
//    val restaurants: StateFlow<List<DetailsOFDestination>> = _restaurants
//
//    private val _attractions = MutableStateFlow<List<DetailsOFDestination>>(emptyList())
//    val attractions: StateFlow<List<DetailsOFDestination>> = _attractions
//
//    // Pagination tokens
//    private var hotelNextToken: String? = null
//    private var restaurantNextToken: String? = null
//    private var attractionNextToken: String? = null
//
//    // Public method to fetch initial data
//    fun fetchInitialData(placeName: String, placeAddress: String) {
//        fetchHotels(placeName, placeAddress)
//        fetchRestaurants(placeName, placeAddress)
//        fetchAttractions(placeName, placeAddress)
//    }
//
//    // Fetch methods for hotels, restaurants, and attractions
//    private fun fetchHotels(placeName: String, placeAddress: String, pageToken: String? = null) {
//        viewModelScope.launch {
//            val result = repository.fetchTopHotels(placeName, placeAddress, pageToken)
//            result.onSuccess {
//                if (pageToken == null) {
//                    _hotels.value = it.places
//                } else {
//                    _hotels.value = _hotels.value + it.places
//                }
//                hotelNextToken = it.nextPageToken
//            }
//        }
//    }
//
//    private fun fetchRestaurants(placeName: String, placeAddress: String, pageToken: String? = null) {
//        viewModelScope.launch {
//            val result = repository.fetchTopRestaurants(placeName, placeAddress, pageToken)
//            result.onSuccess {
//                if (pageToken == null) {
//                    _restaurants.value = it.places
//                } else {
//                    _restaurants.value = _restaurants.value + it.places
//                }
//                restaurantNextToken = it.nextPageToken
//            }
//        }
//    }
//
//    private fun fetchAttractions(placeName: String, placeAddress: String, pageToken: String? = null) {
//        viewModelScope.launch {
//            val result = repository.fetchTopAttractions(placeName, placeAddress, pageToken)
//            result.onSuccess {
//                if (pageToken == null) {
//                    _attractions.value = it.places
//                } else {
//                    _attractions.value = _attractions.value + it.places
//                }
//                attractionNextToken = it.nextPageToken
//            }
//        }
//    }
//
//    // Public "load more" triggers
//    fun loadMoreHotels(placeName: String, placeAddress: String) {
//        hotelNextToken?.let {
//            viewModelScope.launch {
//                kotlinx.coroutines.delay(2000) // Google's token takes ~2s to activate
//                fetchHotels(placeName, placeAddress, it)
//            }
//        }
//    }
//
//    fun loadMoreRestaurants(placeName: String, placeAddress: String) {
//        restaurantNextToken?.let {
//            viewModelScope.launch {
//                kotlinx.coroutines.delay(2000)
//                fetchRestaurants(placeName, placeAddress, it)
//            }
//        }
//    }
//
//    fun loadMoreAttractions(placeName: String, placeAddress: String) {
//        attractionNextToken?.let {
//            viewModelScope.launch {
//                kotlinx.coroutines.delay(2000)
//                fetchAttractions(placeName, placeAddress, it)
//            }
//        }
//    }
//}

