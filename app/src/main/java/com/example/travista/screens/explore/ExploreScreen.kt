package com.example.travista.screens.explore


import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
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




