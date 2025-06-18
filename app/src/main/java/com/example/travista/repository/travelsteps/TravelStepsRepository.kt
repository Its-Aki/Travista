package com.example.travista.repository.travelsteps

import android.util.Log
import com.example.travista.data.getGeminiApiKey
import com.example.travista.data.tripsteps.Content
import com.example.travista.data.tripsteps.GeminiRequest
import com.example.travista.data.tripsteps.Part
import com.example.travista.data.tripsteps.TravelSteps
import com.example.travista.remote.GeminiApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class TravelStepsRepository @Inject constructor(private val apiService: GeminiApiService) {

    suspend fun fetchTravelSteps(query: String, apiKey: String = getGeminiApiKey()): Result<TravelSteps>? {
        return withContext(Dispatchers.IO) {
            try {
                val request = GeminiRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(Part(text = query))
                        )
                    )
                )

                val response = apiService.getTravelPlan(apiKey = apiKey, request = request)
                Log.d("travel", "Summary from Gemini API: ${response ?: "No summary received"}")
                if (response == null) {
                    return@withContext Result.failure(Exception("API call returned null response"))
                }

                if (response.isSuccessful) {
                    val rawJson = response.body()
                        ?.candidates
                        ?.firstOrNull()
                        ?.content
                        ?.parts
                        ?.firstOrNull()
                        ?.text

                    println("Gemini raw response text: $rawJson")

                    if (!rawJson.isNullOrBlank()) {
                        // First, clean string if wrapped in quotes
                        val cleanJson = rawJson.trim().removePrefix("```json").removeSuffix("```").trim()

                        // If the result is a JSON string (escaped), unescape it
//                        val actualJson = if (cleanJson.startsWith("\"") && cleanJson.endsWith("\"")) {
//                            Gson().fromJson(cleanJson, String::class.java)
//                        } else {
//                            cleanJson
//                        }
                        val actualJson = cleanJson

                        val travelSteps = Gson().fromJson(actualJson, TravelSteps::class.java)

                        if (travelSteps.route.isNotEmpty()) {
                            return@withContext Result.success(travelSteps)
                        } else {
                            return@withContext Result.failure(Exception("No data found in route"))
                        }
                    } else {
                        return@withContext Result.failure(Exception("Empty or invalid response text"))
                    }

                } else {
                    return@withContext Result.failure(Exception("API request failed: ${response.message()}"))
                }
            } catch (e: IOException) {
                return@withContext Result.failure(Exception("No internet connection"))
            } catch (e: HttpException) {
                return@withContext Result.failure(Exception("API error: ${e.message()}"))
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext Result.failure(Exception("Something went wrong: ${e.localizedMessage}"))
            }
        }
    }
}
