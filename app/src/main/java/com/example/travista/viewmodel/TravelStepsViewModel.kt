package com.example.travista.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travista.data.tripsteps.Content
import com.example.travista.data.tripsteps.GeminiRequest
import com.example.travista.data.tripsteps.Part
import com.example.travista.data.tripsteps.TravelSteps
import com.example.travista.repository.travelsteps.TravelStepsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TravelStepsViewModel @Inject constructor(private val repository: TravelStepsRepository) : ViewModel() {

    private val _travelDetails = MutableStateFlow<Result<TravelSteps>?>(null)
    val travelDetails: MutableStateFlow<Result<TravelSteps>?> = _travelDetails

    fun fetchTravelDetails(startingAddress: String, destinationAddress: String) {
        // it allows us to define a coroutine within the scope of viewmodel
        viewModelScope.launch {


              val prompt = """
            You are an intelligent and helpful travel assistant.

I will provide you with a **starting address** and a **destination address**. Based on this information, generate a detailed **step-by-step travel plan** that guides a traveler from the starting location to the final destination.
Make sure that all the information provided is up to date and valid that trains, routes, flights,ships actually use that route to travel. Check first that this route and available train stations and airports and others actually go to to a valid destination and actually operate.
Make sure to provide only the required fields in the response.Don't give extra responses. Only give responses in the json format only. 
Each step in the journey must include the following fields:

- `stepIndex`: the index number of the step, starting from 1  
- `title`: a short and descriptive title for this step (e.g., "Take a train to Delhi")  
- `travelMode`: the mode of transportation used (e.g., Flight, Road, Train, Bus, Walk, etc.)  
- `distance`: the estimated or approximate distance for this leg (e.g., ~ 25 -50 km(give response in exactly same format,no extra contents)  
- `duration`: the approximate time to complete this leg (e.g., "1 hour 30 minutes")  
- `to`: the destination address or location for this step (in a combined text format such as name + full address). if the destination is not clear, just respond with Go to your desired location. 
- `summary`: a concise explanation (maximum 500 words) of what this step involves. Include realistic travel advice, things to consider, and useful context to help the traveler make informed decisions. Analyze current news for that location and include relevant information for the traveler.  
- `guides`: a rich text paragraph (not a list) containing helpful travel tips, directions, insights, or recommendations specific to this step. Write this in a natural, sentence-based format and ensure it is distinct from the summary. It should feel conversational and informative, containing 2–4 sentences.

Return the response in the following **structured JSON** format, suitable for frontend display:
Each step in the journey must include: step index wil be int and all others will be strings. Make sure to return the results in the same format provided in examples no extra words or so.

```json
{
  "route": [
    {
      "stepIndex": 1,
      "title": "Take a cab to Madgaon Railway Station",
      "travelMode": "Road",
      "distance": "~ 25 km",
      "duration": "40 minutes",
      "to": "Madgaon Railway Station, South Goa, Goa, India",
      "summary": "Start your journey by taking a cab from Goa Airport to Madgaon Railway Station. This drive takes you through the heart of Goa and usually takes around 40 minutes depending on traffic conditions. Taxis are readily available at the airport, or you can book one using ride-hailing apps. Due to recent roadwork near Verna, some delays may occur during the day.",
      "guides": "Make sure to check cab availability during peak hours. Having a backup travel app or local taxi number is recommended in case of unavailability. Early morning rides are often faster due to less traffic."
    }
  ]
}

            START: $startingAddress
            DESTINATION: $destinationAddress
            
          
            """.trimIndent()




            _travelDetails.value = repository.fetchTravelSteps(query=prompt)
        }
    }
}










//    function to build prompt for gemini api
    fun buildGeminiPrompt(startingAddress: String, destinationAddress: String): String {
        return "give me travel plan from $startingAddress to $destinationAddress with all the steps and the required fields of the response in json format as mentioned "
    }
// "Respond ONLY with JSON and do not wrap the response in a string or markdown. Format like: { "route": [ ... ] }"