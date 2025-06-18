package com.example.travista

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp  // Required if using Hilt for Dependency Injection
class TravistaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize global services here, e.g., Firebase
    }
}
