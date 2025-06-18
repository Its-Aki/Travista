package com.example.travista.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travista.data.nearbylocations.NearbyLocations
import com.example.travista.repository.nearbylocations.NearbyLocationsRepository
import com.example.travista.utils.UserLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NearbyLocationsViewModel @Inject constructor(
    private val repository: NearbyLocationsRepository
) : ViewModel() {

    private val _nearbyLocationsData = MutableStateFlow<Result<List<NearbyLocations>>>(Result.success(emptyList()))
    val nearbyLocationsData: StateFlow<Result<List<NearbyLocations>>> = _nearbyLocationsData

    // Add user location to ViewModel
    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation: StateFlow<UserLocation?> = _userLocation


    // to avoid re-fetching data everytime
    private var hasFetchedData = false

    fun fetchNearbyLocationsData(location:String,placeType:String,radius:Int, userLoc: UserLocation?) {
        if (hasFetchedData) return

        viewModelScope.launch {
            _userLocation.value = userLoc
            _nearbyLocationsData.value = repository.fetchNearbyPlaces(location,placeType,radius)
            hasFetchedData = true
        }
    }

}