package com.example.travista.ui.screen


import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.travista.theme.TravistaTheme


@Composable
    fun ExploreScreen(navController: NavController) {

    SearchScreen(navController = navController)

    }

    @Preview(showBackground = true)
    @Composable
    fun ExploreScreenPreview(){
        TravistaTheme {
          //  ExploreScreen(navController = NavController(LocalContext.current))
        }
    }




