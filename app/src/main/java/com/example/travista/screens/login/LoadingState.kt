package com.example.travista.screens.login

sealed class AuthResult {
    data object Success : AuthResult()
    data class Error(val errorMessage: String) : AuthResult()
    data object Loading : AuthResult()
    data object Idle : AuthResult() // Initial state
}
