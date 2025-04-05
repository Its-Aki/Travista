package com.example.travista.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

// ✅ Function to Check Real Internet Connectivity (Using HTTP Request)
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

//    it checks if the network claims to have internet
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
// Ensures the network really has internet access
            && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}



    
