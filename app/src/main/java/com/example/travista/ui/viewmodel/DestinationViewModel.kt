package com.example.travista.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travista.data.detailsofdestinaton.DetailsOFDestination
import com.example.travista.repository.detailsofdestination.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DestinationViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    private val _topHotelData = MutableStateFlow<Result<List<DetailsOFDestination>>>(Result.success(emptyList()))
    val topHotelData: StateFlow<Result<List<DetailsOFDestination>>> = _topHotelData

    private val _topRestaurantData = MutableStateFlow<Result<List<DetailsOFDestination>>>(Result.success(emptyList()))
    val topRestaurantData: StateFlow<Result<List<DetailsOFDestination>>> = _topRestaurantData

    private val _topAttractionsData = MutableStateFlow<Result<List<DetailsOFDestination>>>(Result.success(emptyList()))
    val topAttractionsData: StateFlow<Result<List<DetailsOFDestination>>> = _topAttractionsData

    private var hasFetchedData = false

    fun fetchDestinationData(placeName: String, placeAddress: String) {
        if (hasFetchedData) return

        viewModelScope.launch {
            _topHotelData.value = placesRepository.fetchTopHotels(placeName, placeAddress)
            _topRestaurantData.value = placesRepository.fetchTopRestaurants(placeName, placeAddress)
            _topAttractionsData.value = placesRepository.fetchTopAttractions(placeName, placeAddress)

            hasFetchedData = true
        }
    }
}
