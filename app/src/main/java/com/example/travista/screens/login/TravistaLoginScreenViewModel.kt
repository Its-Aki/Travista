package com.example.travista.screens.login

import android.app.Activity // For context type check
import android.content.Context
import android.util.Log
// Removed: import androidx.core.content.ContextCompat.getString // Not needed this way
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCustomException // Specific exception
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travista.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

// import com.google.firebase.Firebase // BoM style
// import com.google.firebase.auth.auth // BoM style
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

// AuthResult sealed interface (assuming it's defined elsewhere or like this)


class AuthViewModel : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("") // For registration
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _fullName = MutableStateFlow("") // For registration
    val fullName: StateFlow<String> = _fullName

    private val _authResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authResult: StateFlow<AuthResult> = _authResult

    // Use by lazy for firebaseAuth if it's only for email/pass
    // For Google Sign-In, the 'auth' instance below is used.
//    private val firebaseAuthInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPass: String) {
        _password.value = newPass
    }

    fun onConfirmPasswordChange(newPass: String) {
        _confirmPassword.value = newPass
    }

    fun onFullNameChange(newName: String) {
        _fullName.value = newName
    }

    fun resetAuthState() {
        _authResult.value = AuthResult.Idle
    }

    fun loginUser() {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            if (_email.value.isBlank() || _password.value.isBlank()) {
                _authResult.value = AuthResult.Error("Email and password cannot be empty.")
                return@launch
            }
            try {
                val authTaskResult = auth.signInWithEmailAndPassword(_email.value.trim(), _password.value).await()
                _authResult.value = AuthResult.Success
                Log.d("AuthVM", "signInWithEmailAndPassword success: User UID: ${authTaskResult.user?.uid}")
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _authResult.value = AuthResult.Error(e.message ?: "Login failed.")
                Log.w("AuthVM", "signInWithEmailAndPassword failure: ${e.localizedMessage}", e)
            }
        }
    }

    fun logoutUser() {
        auth.signOut()
        Log.d("AuthVM", "User logged out.")
        resetAuthState()

    }

    fun registerUser() {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            // ... (validation checks as before, adding error codes)
            if (_fullName.value.isBlank() || _email.value.isBlank() || _password.value.isBlank() || _confirmPassword.value.isBlank()) {
                _authResult.value = AuthResult.Error("All fields are required.")
                return@launch
            }
            if (_password.value != _confirmPassword.value) {
                _authResult.value = AuthResult.Error("Passwords do not match.")
                return@launch
            }
            if (_password.value.length < 6) {
                _authResult.value = AuthResult.Error("Password must be at least 6 characters.")
                return@launch
            }

            try {
                auth.createUserWithEmailAndPassword(_email.value.trim(), _password.value).await()
                _authResult.value = AuthResult.Success
                Log.d("AuthVM", "createUserWithEmailAndPassword success")
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _authResult.value = AuthResult.Error(e.message ?: "Registration failed.")
                Log.w("AuthVM", "createUserWithEmailAndPassword failure: ${e.localizedMessage}", e)
            }
        }
    }

    // --- Google Sign-In ---
    private lateinit var credentialManager: CredentialManager
    // Use FirebaseAuth.getInstance() for consistency or Firebase.auth if using BoM

    fun initAndSignInWithGoogle(context: Context) { // Renamed for clarity
        // It's good practice to ensure the context is an Activity context for CredentialManager UI
        if (context !is Activity) {
            Log.w("AuthVM_Google", "Warning: Context for Google Sign-In is not an Activity. UI may not show correctly.")
            // Potentially set an error or proceed with caution
        }

        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            try {
                if (!this@AuthViewModel::credentialManager.isInitialized) { // Check if initialized
                    Log.d("AuthVM_Google", "Attempting initialization")

                    credentialManager = CredentialManager.create(context)
                }
                Log.d("AuthVM_Google", "Attempting failed initialization")

                val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(context.getString(R.string.web_client_id)) // Correct way to get string resource
                    .setFilterByAuthorizedAccounts(false) // Good for initial sign-in
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                Log.d("AuthVM_Google", "Attempting credentialManager.getCredential")
                val result = credentialManager.getCredential(
                    request = request,
                    context = context, // This context is used by CredentialManager
                )
                handleGoogleSignInResponse(result) // Changed name for clarity
            } catch (e: GetCredentialCustomException) {
                // Specific catch for the error you're seeing!
                Log.e("AuthVM_Google", "GetCredentialCustomException: Type: ${e.type}, Message: ${e.message}", e)
                // The message "[28444] Developer console is not set up correctly." comes from here.
                _authResult.value = AuthResult.Error(
                    "Google Sign-In configuration error. Please check developer console setup. (Type: ${e.type})"// e.g., "androidx.credentials.exceptions.NO_CREDENTIAL" or a custom one from Google
                )
            } catch (e: GetCredentialException) { // Catch other GetCredentialExceptions
                Log.e("AuthVM_Google", "GetCredentialException: Type: ${e.type}, Message: ${e.message}", e)
                _authResult.value = AuthResult.Error(
                    "Google Sign-In failed. (Type: ${e.type})"
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("AuthVM_Google", "General error during Google Sign-In init: ${e.message}", e)
                _authResult.value = AuthResult.Error(e.message ?: "An unexpected error occurred.")
            }
        }
    }

    private suspend fun handleGoogleSignInResponse(result: GetCredentialResponse) {
        Log.d("AuthVM_Google", "Handling Google Sign-In response. Credential type: ${result.credential.type}")
        when (val credential = result.credential) {
            // You are using androidx.credentials.GoogleIdTokenCredential, so check its type constant
            is CustomCredential -> {
                // The type string for Google ID tokens from androidx.credentials.GetGoogleIdOption
                // when wrapped in CustomCredential is often GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        Log.d("AuthVM_Google", "Parsing Google ID token from CustomCredential data.")
                        // Use androidx.credentials.GoogleIdTokenCredential.createFrom
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        Log.d("AuthVM_Google", "Received Google ID token via CustomCredential.")
                        // Log.v("AuthVM_Google", "ID Token: $idToken") // Careful logging tokens
                        loginWithFirebaseUsingGoogleToken(idToken)
                    } catch (e: GoogleIdTokenParsingException) { // From androidx.credentials
                        Log.e("AuthVM_Google", "Invalid Google ID token from CustomCredential (androidx.credentials).", e)
                        _authResult.value = AuthResult.Error(e.message ?: "Failed to parse Google ID token.")
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        Log.e("AuthVM_Google", "Error processing CustomCredential data for Google Sign-In: ${e.message}", e)
                        _authResult.value = AuthResult.Error(e.message ?: "Error processing Google Sign-In data.")
                    }
                } else {
                    Log.e("AuthVM_Google", "CustomCredential has unexpected type for Google Sign-In: ${credential.type}")
                    _authResult.value = AuthResult.Error("Google Sign-In failed: Unexpected data format.")
                }
            }
            is GoogleIdTokenCredential -> { // Direct credential (less common for initial One Tap UI, but possible)
                try {
                    Log.d("AuthVM_Google", "Direct GoogleIdTokenCredential received.")
                    val idToken = credential.idToken
                    loginWithFirebaseUsingGoogleToken(idToken)
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    Log.e("AuthVM_Google", "Error processing direct GoogleIdTokenCredential: ${e.message}", e)
                    _authResult.value = AuthResult.Error(e.message ?: "Error processing Google Sign-In data.")
                }
            }
            else -> {
                Log.e("AuthVM_Google", "Unexpected credential type after Google Sign-In: ${result.credential.javaClass.name}")
                _authResult.value = AuthResult.Error("Google Sign-In failed: Unexpected data result.")
            }
        }
    }

    private suspend fun loginWithFirebaseUsingGoogleToken(idToken: String) { // Renamed
        try {
            Log.d("AuthVM_Google", "Attempting Firebase sign-in with Google token.")
            val googleCredentials = GoogleAuthProvider.getCredential(idToken, null)
            // Using 'auth' instance declared in this ViewModel for Google Sign-In
            val user = auth.signInWithCredential(googleCredentials).await().user
            Log.d(
                "AuthVM_Google", """
                Successfully signed in with Google & Firebase. User: ${user?.uid}
                Email: ${user?.email}, Name: ${user?.displayName}
            """.trimIndent()
            )
            _authResult.value = AuthResult.Success
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e("AuthVM_Google", "Firebase sign-in with Google token failed.", e)
            _authResult.value = AuthResult.Error(e.message ?: "Firebase authentication with Google failed.")
        }
    }
    override fun onCleared() {
        super.onCleared()
//        if (this::credentialManager.isInitialized) {
//            try {
//                credentialManager.cancel()// From androidx.credentials
//                Log.d("AuthVM_Google", "CredentialManager.cancel() called in onCleared.")
//            } catch (e: Exception) {
//                Log.w("AuthVM_Google", "Error during credentialManager.cancel() in onCleared", e)
//            }
//        }
    }
}