package com.example.travista.remote


import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class ApiCallInterceptor : Interceptor {

    companion object {
        private val apiCallCount = mutableMapOf<String, Int>()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val urlPath = request.url.encodedPath // Only the path (like /maps/api/place/details/json)

        // Update the count
        val currentCount = apiCallCount[urlPath] ?: 0
        apiCallCount[urlPath] = currentCount + 1

        // Log the request path and count
        Log.d("api_call_log", "Endpoint: $urlPath | Call Count: ${apiCallCount[urlPath]}")

        return chain.proceed(request)
    }
}
