package com.example.travista.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travista.data.getApikey
import com.example.travista.data.model.Prediction
import com.example.travista.remote.PlacesApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AutocompleteViewModel @Inject constructor(
    private val apiService: PlacesApiService
) : ViewModel() {

    private val _suggestions = MutableStateFlow<List<Prediction>>(emptyList())
    val suggestions: StateFlow<List<Prediction>> = _suggestions

    // In-memory LRU cache (max 100 entries)
    private val queryCache = object : LinkedHashMap<String, List<Prediction>>(100, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, List<Prediction>>): Boolean {
            return size > 100 // Keep only latest 100 queries
        }
    }

    fun fetchSuggestions(query: String) {
        viewModelScope.launch {
            // Return cached data if available
            if (queryCache.containsKey(query)) {
                _suggestions.value = queryCache[query] ?: emptyList()
                return@launch
            }

            try {
                val response = apiService.getPlaceSuggestions(query, getApikey())

                if (response.status == "OK") {
                    val filtered = response.predictions.filter {
                        val formatting = it.structured_formatting
                        formatting.main_text.isNotBlank() && formatting.secondary_text.isNotBlank()
                    }

                    queryCache[query] = filtered
                    _suggestions.value = filtered
                } else {
                    _suggestions.value = emptyList()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _suggestions.value = emptyList()
            }
        }
    }

    // Clear the cache manually if needed
    fun clearCache() {
        queryCache.clear()
    }

    // Automatically clear cache when app closes and ViewModel is destroyed
    override fun onCleared() {
        super.onCleared()
        clearCache()
    }
}
