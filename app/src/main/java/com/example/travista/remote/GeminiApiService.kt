package com.example.travista.remote


import com.example.travista.data.tripsteps.GeminiRequest

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {

    // suspend functions
    @Headers("Content-Type: application/json")
    @POST("v1/models/gemini-1.5-flash-002:generateContent")
    suspend fun getTravelPlan(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiContentResponse>?
}


// Response wrappers
data class GeminiContentResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val content: Content
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String // <-- This is a stringified JSON (e.g., "{ \"route\": [...] }")
)

