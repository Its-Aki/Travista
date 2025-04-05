package com.example.travista

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import com.example.travista.theme.TravistaTheme
import com.example.travista.ui.screen.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravistaTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize()

                ) {
                    MainScreen() // Load the Main Screen
                }
            }
        }
    }
}




