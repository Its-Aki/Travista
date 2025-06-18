package com.example.travista.screens.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.travista.R
import kotlinx.coroutines.launch

private const val WEB_CLIENT_ID = "AIzaSyBTEmAmf5uSKg0NgktuJODTsC6cuRZjOXc"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    rootNavController: NavController, // Renamed from rootNavController for consistency if this is the local nav controller
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    val context = LocalContext.current // Use MainActivity's context
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val email by authViewModel.email.collectAsState()
    val password by authViewModel.password.collectAsState()
    val authResult by authViewModel.authResult.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }


    // Handle AuthResult changes (show messages, navigate)
    LaunchedEffect(authResult) {
        when (val result = authResult) {
            is AuthResult.Success -> {
                Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }
            is AuthResult.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = result.errorMessage,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            AuthResult.Loading -> { /* Optionally show a global loading indicator if not button-specific */ }
            AuthResult.Idle -> { /* Do nothing */ }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with your app logo
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(2f)
                    .padding(bottom = 4.dp)
            )
            Text("Welcome Back!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Login to continue your adventure", style = MaterialTheme.typography.bodyMedium, color = Color.Gray, modifier = Modifier.padding(bottom = 32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { authViewModel.onEmailChange(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email Icon") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                isError = authResult is AuthResult.Error && (authResult as AuthResult.Error).errorMessage.contains("Email", ignoreCase = true)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { authViewModel.onPasswordChange(it) },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password Icon") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    if (email.isNotBlank() && password.isNotBlank()) authViewModel.loginUser()
                }),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    val description = if (passwordVisible) "Hide password" else "Show password"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                isError = authResult is AuthResult.Error && (authResult as AuthResult.Error).errorMessage.contains("password", ignoreCase = true)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = {
                    // TODO: Implement Forgot Password
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Forgot Password clicked (not implemented)")
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot Password?")
            }
            Spacer(modifier = Modifier.height(24.dp))

            val isEmailPassLoginLoading = authResult == AuthResult.Loading &&
                    (email.isNotBlank() || password.isNotBlank()) // Crude check if this was the source

            if (isEmailPassLoginLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        keyboardController?.hide()
                        authViewModel.loginUser()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = email.isNotBlank() && password.isNotBlank() && authResult != AuthResult.Loading
                ) {
                    Text("LOGIN", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Or sign in with", color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            val isGoogleLoginLoading = authResult == AuthResult.Loading && !isEmailPassLoginLoading

            if (isGoogleLoginLoading) {
                CircularProgressIndicator()
            } else {
                OutlinedButton(
                    onClick = {
                        keyboardController?.hide() // Hide keyboard before starting external activity
                        authViewModel.initAndSignInWithGoogle(context)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = authResult != AuthResult.Loading
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google_logo), // Add a google logo to your drawables
                        contentDescription = "Google Sign-In",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified // Use original colors
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign in with Google")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?")
                TextButton(onClick = onNavigateToRegister) {
                    Text("Register here", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

    // Example: If using Firebase for backend Google Sign-In


