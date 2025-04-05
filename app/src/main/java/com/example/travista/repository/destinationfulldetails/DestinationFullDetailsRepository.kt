package com.example.travista.repository.destinationfulldetails



import com.example.travista.data.getApikey
import com.example.travista.data.detinationfulldetails.DestinationFullDetails
import com.example.travista.remote.PlacesApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class DestinationFullDetailsRepository @Inject constructor(private val apiService: PlacesApiService) {

    suspend fun fetchDestinationFullDetails(placeId: String, apiKey: String= getApikey()): Result<DestinationFullDetails>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDestinationFullDetails(placeId, apiKey)
                if (response.isSuccessful) {
                    response.body()?.destinationFullDetails?.let {
                        return@withContext Result.success(it)
                    }
                    return@withContext Result.failure(Exception("No data found"))
                } else {
                    return@withContext Result.failure(Exception("API request failed: ${response.message()}"))
                }
            } catch (e: IOException) {
                return@withContext Result.failure(Exception("No internet hai ji  connection"))
            } catch (e: HttpException) {
                return@withContext Result.failure(Exception("API error: ${e.message()}"))
            } catch (e: Exception) {
                return@withContext Result.failure(Exception("Something went wrong"))
            }
        }
    }
}
