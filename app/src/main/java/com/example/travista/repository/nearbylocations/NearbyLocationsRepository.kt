package com.example.travista.repository.nearbylocations

import android.util.Log
import com.example.travista.data.getApikey
import com.example.travista.data.nearbylocations.NearbyLocations
import com.example.travista.remote.PlacesApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NearbyLocationsRepository @Inject constructor(
    private val apiService: PlacesApiService
) {

    suspend fun fetchNearbyPlaces(
        location: String,          // format: "lat,lng"
        placeType: String,         // e.g. "restaurant", "hotel", "atm"
        radius: Int       // default radius in meters

    ): Result<List<NearbyLocations>> {
        return withContext(Dispatchers.IO) {
            try {
                val apiKey = getApikey()

                val response = apiService.getNearbyPlaces(
                    location = location,
                    radius = radius,
                    type = placeType,
                    apiKey = apiKey
                )

//                val responseBodyString = response.body()
//                Log.d("nearbylocations", "API Response Body: $responseBodyString")
                 Log.d("delete", response.toString())

                if (response.isSuccessful) {
                    val nearbyPlaces = response.body()?.nearbyLocations.orEmpty()
//                    Log.d("nearbylocations", "Places List Size: ${nearbyPlaces.size}")

                    return@withContext if (nearbyPlaces.isNotEmpty()) {
                        Result.success(nearbyPlaces)


                    } else {
                        Result.failure(Exception("No results found for type: $placeType"))
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
