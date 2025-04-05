package com.example.travista.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.travista.data.getApikey
import com.example.travista.theme.TravistaTheme

@Composable
fun TripsScreen() {
    val scrollState = rememberScrollState()
    // /* for hotels
     val sampleApiKey = getApikey()
    val samplePlaceId = "ChIJo9jLZ7id9DkRXoH-O-TR3QQ"


   //    */
    // for hazaribagh jheel samplePlaceId = "ChIJiZxrfiSc9DkRCkVctf9k5EE"

    // for bus stand  samplePlaceId = "ChIJuU7NZQWc9DkROeebNWrURAE"

    /*


    val samplePlaceId = "ChIJo9jLZ7id9DkRXoH-O-TR3QQ"
    //// Replace with an actual Place ID
    val sampleApiKey = API_KEY

     */

    // for restaurant

// vanbhojan   samplePlaceId = "ChIJo9jLZ7id9DkRXoH-O-TR3QQ"
    // el tina harlem samplePlaceId = "ChIJMRtWS2r2wokRu_wtCMfge6Y"
    // prem hotel   samplePlaceId =   "ChIJT9MD3Zyd9DkRAlmduVZaslU"
     // samplePlaceId = "ChIJ93QrUr2uEmsR36wFzbuOwW4"
    // samplePlaceId = "ChIJ4yL4JOWd9DkR_QpKSbw4TmU"
    // error one  samplePlaceId =  "ChIJ-RrjUkuZ9DkRNhY2ek49wgQ"
// samplePlaceId ="ChIJo75VIBeuEmsRi1UBARIHKL0"


    // for hotels
     //      samplePlaceId = "ChIJQV8DScWd9DkRag0YgI2ty9k"
    // samplePlaceId = "ChIJDXsf19dYWjcRagXRo68vpkw"
    // for everest
    //mount everest   -- >   samplePlaceId = "ChIJvZ69FaJU6DkRsrqrBvjcdgU"
    Column(Modifier.verticalScroll(scrollState)) {
     //   HotelScreen(samplePlaceId,sampleApiKey)

        DestinationFullDetailsScreen(samplePlaceId,sampleApiKey)
      //   HotelScreen("ChIJvZ69FaJU6DkRsrqrBvjcdgU",sampleApiKey)



    }
}



@Preview(showBackground = true)
@Composable
fun TripsScreenPreview() {
    TravistaTheme {
        TripsScreen()
    }
}
