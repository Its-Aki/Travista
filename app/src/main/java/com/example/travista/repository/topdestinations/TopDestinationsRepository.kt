package com.example.travista.repository.topdestinations

import com.example.travista.data.CityQueryGenerator
import com.example.travista.data.getApikey
import com.example.travista.remote.PlacesApiService
import com.example.travista.room.TopDestinationsDao
import com.example.travista.room.TopDestinationsEntity
import com.example.travista.room.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class TopDestinationsRepository @Inject constructor(
    private val apiService: PlacesApiService,
    private val dao: TopDestinationsDao
) {

    private val CACHE_EXPIRATION_TIME = 24 * 60 * 60 * 1000L // 24 hours

    suspend fun fetchTopDestinations(): Result<List<TopDestinationsEntity>> {
        return withContext(Dispatchers.IO) {
            try {
                val currentTime = System.currentTimeMillis()
                val cached = dao.getAllDestinations()
                    .filter { currentTime - it.timestamp < CACHE_EXPIRATION_TIME }

                if (cached.isNotEmpty()) {
                    return@withContext Result.success(cached)
                }

                val query = CityQueryGenerator.getRandomCityQuery()
                val response = apiService.getTopDestinations(query = query, apiKey = getApikey())

                if (response.isSuccessful) {
                    val destinationList = response.body()?.topDestinations.orEmpty()
                        .filter { !it.placeId.isNullOrEmpty() }

                    return@withContext if (destinationList.isNotEmpty()) {
                        dao.clearAll()
                        dao.insertTopDestinations(destinationList.map { it.toEntity() })
                        Result.success(dao.getAllDestinations())
                    } else {
                        Result.failure(Exception("No results found for: $query"))
                    }
                }

                val errorMessage = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("API request failed: $errorMessage"))

            } catch (e: IOException) {
                Result.failure(Exception("No internet connection. Please check your network."))
            } catch (e: HttpException) {
                Result.failure(Exception("API error: ${e.response()?.errorBody()?.string() ?: e.message()}"))
            } catch (e: Exception) {
                Result.failure(Exception("Something went wrong: ${e.localizedMessage}"))
            }
        }
    }
}
