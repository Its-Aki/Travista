package com.example.travista.repository.detailsofdestination

import com.example.travista.data.detailsofdestinaton.DetailsOFDestination
import com.example.travista.data.getApikey
import com.example.travista.remote.PlacesApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PlacesRepository @Inject constructor(
    private val apiService: PlacesApiService
) {
    suspend fun fetchTopHotels(placeName: String, placeAddress: String): Result<List<DetailsOFDestination>> {
        return fetchPlaces("top hotels near $placeName, $placeAddress")
    }

    suspend fun fetchTopRestaurants(placeName: String, placeAddress: String): Result<List<DetailsOFDestination>> {
        return fetchPlaces("top restaurants near $placeName, $placeAddress")
    }

    suspend fun fetchTopAttractions(placeName: String, placeAddress: String): Result<List<DetailsOFDestination>> {
        return fetchPlaces("top things to do near $placeName, $placeAddress")
    }

    private suspend fun fetchPlaces(query: String): Result<List<DetailsOFDestination>> {
        return withContext(Dispatchers.IO) {
            try {
                val apiKey = getApikey()
                val response = apiService.getDestinationDetails(query, apiKey) // Using same API

                if (response.isSuccessful) {
                    val placesList = response.body()?.detailsOfDestination.orEmpty()

                    return@withContext if (placesList.isNotEmpty()) {
                        Result.success(placesList)
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
