package com.example.travista.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travista.data.detinationfulldetails.DestinationFullDetails
import com.example.travista.repository.destinationfulldetails.DestinationFullDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DestinationFullDetailsViewModel @Inject constructor(private val repository: DestinationFullDetailsRepository) : ViewModel() {

    private val _destinationFullDetails = MutableStateFlow<Result<DestinationFullDetails>?>(null)
    val destinationFullDetails: StateFlow<Result<DestinationFullDetails>?> = _destinationFullDetails
//    val destinationFullDetails: StateFlow<Result<DestinationFullDetails>>?> = _destinationFullDetails

    fun fetchDestinationFullDetails(placeId: String, apiKey: String) {
        // it allows to define a coroutine within the scope of viewmodel
        viewModelScope.launch {
            _destinationFullDetails.value = repository.fetchDestinationFullDetails(placeId, apiKey)
        }
    }
}
