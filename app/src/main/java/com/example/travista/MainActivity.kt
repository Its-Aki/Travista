package com.example.travista

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.travista.navigation.AppNavigation
import com.example.travista.theme.TravistaTheme
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
                    val rootNavController = rememberNavController()
                    AppNavigation(rootNavController,applicationContext)
                }
            }
        }
    }
}





