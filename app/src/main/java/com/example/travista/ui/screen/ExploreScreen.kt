package com.example.travista.ui.screen


import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.travista.theme.TravistaTheme


@Composable
    fun ExploreScreen() {
    SearchScreen()
    }

    @Preview(showBackground = true)
    @Composable
    fun ExploreScreenPreview(){
        TravistaTheme {
            ExploreScreen()
        }
    }




