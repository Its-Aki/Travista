package com.example.travista.viewmodel.topdestinations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travista.repository.topdestinations.TopDestinationsRepository
import com.example.travista.room.TopDestinationsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopDestinationsViewModel @Inject constructor(
    private val repository: TopDestinationsRepository
) : ViewModel() {

    // UI State sealed class
    sealed class TopDestinationsUiState {
        object Loading : TopDestinationsUiState()
        data class Success(val data: List<TopDestinationsEntity>) : TopDestinationsUiState()
        data class Error(val message: String) : TopDestinationsUiState()
    }

    private val _topDestinationsState = MutableStateFlow<TopDestinationsUiState>(TopDestinationsUiState.Loading)
    val topDestinationsState: StateFlow<TopDestinationsUiState> = _topDestinationsState

    init {
        fetchTopDestinations()
    }

    fun fetchTopDestinations() {
        viewModelScope.launch {
            _topDestinationsState.value = TopDestinationsUiState.Loading
            val result = repository.fetchTopDestinations()
            result.fold(
                onSuccess = { list ->
                    _topDestinationsState.value = TopDestinationsUiState.Success(list)
                },
                onFailure = { error ->
                    _topDestinationsState.value = TopDestinationsUiState.Error(
                        error.message ?: "Unknown error occurred"
                    )
                }
            )
        }
    }
}
