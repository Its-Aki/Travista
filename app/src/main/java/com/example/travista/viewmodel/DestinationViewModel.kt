package com.example.travista.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travista.data.detailsofdestinaton.DetailsOFDestination
import com.example.travista.repository.detailsofdestination.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DestinationViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    // list of hotels, restaurants and attraction
    private val _topHotelData =
        MutableStateFlow<Result<List<DetailsOFDestination>>>(Result.success(emptyList()))
    val topHotelData: StateFlow<Result<List<DetailsOFDestination>>> = _topHotelData

    private val _topRestaurantData =
        MutableStateFlow<Result<List<DetailsOFDestination>>>(Result.success(emptyList()))
    val topRestaurantData: StateFlow<Result<List<DetailsOFDestination>>> = _topRestaurantData

    private val _topAttractionsData =
        MutableStateFlow<Result<List<DetailsOFDestination>>>(Result.success(emptyList()))
    val topAttractionsData: StateFlow<Result<List<DetailsOFDestination>>> = _topAttractionsData

    // tokens
    private val _hotelNextToken = MutableStateFlow<String?>(null)

    private val _restaurantNextToken = MutableStateFlow<String?>(null)

    private val _attractionNextToken = MutableStateFlow<String?>(null)


    // place summary
    private val _placeSummary = MutableStateFlow<Result<String>>(Result.success(""))
    val placeSummary: StateFlow<Result<String>> = _placeSummary




    private var hasFetchedData = false
    fun fetchDestinationData(placeName: String, placeAddress: String) {
        if(hasFetchedData) return
        fetchHotels(placeName, placeAddress, _hotelNextToken.value)
        fetchRestaurants(placeName, placeAddress, _restaurantNextToken.value)
        fetchAttractions(placeName, placeAddress, _attractionNextToken.value)
        fetchPlaceSummaryForAddress(placeName,placeAddress)
        hasFetchedData = true

    }

    // fetch summary and pass prompt

    fun fetchPlaceSummaryForAddress(placeName:String,address: String) {
        viewModelScope.launch {
            val prompt = """
                Provide a tourist-friendly summary of "$placeName,$address" in less than 200 words.
                Include historical background, top attractions, cultural significance, and key highlights for travelers.
                If no useful summary is available or the location is  or any other issue, return an empty string only .
            """.trimIndent()

            _placeSummary.value = placesRepository.fetchPlaceSummary(prompt)
        }
    }


    // Fetch methods for hotels, restaurants, and attractions
    fun fetchHotels(placeName: String, placeAddress: String, pageToken: String? = null) {
        viewModelScope.launch {
            val result = placesRepository.fetchTopHotels(placeName, placeAddress, pageToken)
            result.onSuccess { response ->
                val currentList = _topHotelData.value.getOrNull().orEmpty()
                val updatedList = if (pageToken == null) {
                    response.detailsOfDestination.orEmpty()
                } else {
                    currentList + response.detailsOfDestination.orEmpty()
                }

                _topHotelData.value = Result.success(updatedList)
                _hotelNextToken.value = response.nextPageToken
            }
        }
    }
    // restaurants
    fun fetchRestaurants(placeName: String, placeAddress: String, pageToken: String? = null) {
        viewModelScope.launch {
            val result = placesRepository.fetchTopRestaurants(placeName, placeAddress, pageToken)
            result.onSuccess { response ->
                val currentList = _topRestaurantData.value.getOrNull().orEmpty()
                val updatedList = if (pageToken == null) {
                    response.detailsOfDestination.orEmpty()
                } else {
                    currentList + response.detailsOfDestination.orEmpty()
                }

                _topRestaurantData.value = Result.success(updatedList)
                _restaurantNextToken.value = response.nextPageToken
            }
        }
    }
    // attractions
    fun fetchAttractions(placeName: String, placeAddress: String, pageToken: String? = null) {
        viewModelScope.launch {
            val result = placesRepository.fetchTopAttractions(placeName, placeAddress, pageToken)
            result.onSuccess { response ->
                val currentList = _topAttractionsData.value.getOrNull().orEmpty()
                val updatedList = if (pageToken == null) {
                    response.detailsOfDestination.orEmpty()
                } else {
                    currentList + response.detailsOfDestination.orEmpty()
                }

                _topAttractionsData.value = Result.success(updatedList)
                _attractionNextToken.value = response.nextPageToken
            }
        }
    }

    // functions to load more data
    // for hotels
    fun loadMoreHotels(placeName: String, placeAddress: String) {
        val nextToken = _hotelNextToken.value

        if (!nextToken.isNullOrEmpty()) {
            viewModelScope.launch {
                delay(2500) // it gives time for Google's nextPageToken to activate. 2secs required
                fetchHotels(placeName, placeAddress, nextToken)
            }
        }
    }
    // for restaurants
    fun loadMoreRestaurants(placeName: String, placeAddress: String) {
        val nextToken = _restaurantNextToken.value

        if (!nextToken.isNullOrEmpty()) {
            viewModelScope.launch {
                delay(2500) // it gives time for Google's nextPageToken to activate. 2secs required
                fetchRestaurants(placeName, placeAddress, nextToken)
            }
        }
    }
    // for attractions
    fun loadMoreAttractions(placeName: String, placeAddress: String) {
        val nextToken = _attractionNextToken.value

        if (!nextToken.isNullOrEmpty()) {
            viewModelScope.launch {
                delay(2500) // it gives time for Google's nextPageToken to activate. 2secs required
                fetchAttractions(placeName, placeAddress, nextToken)
            }
        }
    }





}

//fun fetchDestinationData(placeName: String, placeAddress: String) {
//    if (hasFetchedData) return
//
//    viewModelScope.launch {
//
//        _topHotelData.value = placesRepository.fetchTopHotels(placeName, placeAddress)
//        _topRestaurantData.value = placesRepository.fetchTopRestaurants(placeName, placeAddress)
//        _topAttractionsData.value = placesRepository.fetchTopAttractions(placeName, placeAddress)
//
//
//
//        hasFetchedData = true
//
//    }
//}
