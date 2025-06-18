package com.example.travista.screens.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileScreenViewModel: ViewModel() {
    val currentUser= FirebaseAuth.getInstance().currentUser
    fun getUserName(): String? {
        return currentUser?.displayName

    }
    fun getUserEmail(): String? {
        return currentUser?.email

    }
    fun getUserPhotoUrl(): String? {
        return currentUser?.photoUrl?.toString()
    }
}