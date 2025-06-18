package com.example.travista.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// DataStore extension
val android.content.Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "travel_preferences")

// Preference keys
object PreferenceKeys {
    val TRAVEL_STYLE = stringSetPreferencesKey("travel_style")
    val TRANSPORTATION = stringSetPreferencesKey("transportation")
    val ACCOMMODATION = stringSetPreferencesKey("accommodation")
    val DIETARY = stringSetPreferencesKey("dietary")
    val PACE = stringSetPreferencesKey("pace")
    val ACTIVITIES = stringSetPreferencesKey("activities")
    val DURATION = stringSetPreferencesKey("duration")
    val CLIMATE = stringSetPreferencesKey("climate")
    val SAFETY = stringSetPreferencesKey("safety")
    val COMPANION = stringSetPreferencesKey("companion")
}

data class PreferenceCategory(
    val title: String,
    val emoji: String,
    val options: List<String>,
    val key: Preferences.Key<Set<String>>,
    val allowMultiple: Boolean = true
)

@Composable
fun TravelPreferencesScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Define all preference categories - ALL allow multiple selections
    val categories = listOf(
        PreferenceCategory(
            title = "Travel Style",
            emoji = "🧭",
            options = listOf("Adventure", "Relaxation", "Cultural", "Luxury", "Budget", "Spiritual", "Romantic"),
            key = PreferenceKeys.TRAVEL_STYLE
        ),
        PreferenceCategory(
            title = "Preferred Transportation",
            emoji = "🚗",
            options = listOf("Flights", "Trains", "Buses", "Self-drive car rental", "Ride-hailing (Uber/Ola/Lyft)", "Walking / Cycling"),
            key = PreferenceKeys.TRANSPORTATION
        ),
        PreferenceCategory(
            title = "Accommodation Type",
            emoji = "🏨",
            options = listOf("Hotel", "Hostel", "Resort", "Guesthouse", "Airbnb / Vacation Rental", "Camping"),
            key = PreferenceKeys.ACCOMMODATION
        ),
        PreferenceCategory(
            title = "Dietary Preference",
            emoji = "🍽️",
            options = listOf("No preference", "Vegetarian", "Vegan", "Halal", "Kosher", "Gluten-free"),
            key = PreferenceKeys.DIETARY
        ),
        PreferenceCategory(
            title = "Pace of Travel",
            emoji = "🗺️",
            options = listOf("Fast-paced (cover many places)", "Balanced (some rest, some activity)", "Slow travel (relaxed, few places)"),
            key = PreferenceKeys.PACE
        ),
        PreferenceCategory(
            title = "Activity Interests",
            emoji = "🧳",
            options = listOf("Sightseeing", "Hiking & Nature", "Shopping", "Museums & History", "Wildlife / Safari", "Beach & Water Sports", "Nightlife & Bars"),
            key = PreferenceKeys.ACTIVITIES
        ),
        PreferenceCategory(
            title = "Preferred Travel Duration",
            emoji = "🗓️",
            options = listOf("Weekend trip (1–3 days)", "Short trip (4–6 days)", "One-week trip (7–9 days)", "Long trip (10+ days)"),
            key = PreferenceKeys.DURATION
        ),
        PreferenceCategory(
            title = "Preferred Climate",
            emoji = "🌤️",
            options = listOf("Warm / Tropical", "Cold / Snowy", "Mild / Spring-like"),
            key = PreferenceKeys.CLIMATE
        ),
        PreferenceCategory(
            title = "Safety Level Preference",
            emoji = "🔐",
            options = listOf("Avoid high-crime areas", "Prioritize child safety", "Prefer women-safe zones", "No specific concern"),
            key = PreferenceKeys.SAFETY
        ),
        PreferenceCategory(
            title = "Travel Companion Type",
            emoji = "👥",
            options = listOf("Solo", "Couple", "Family with kids", "Friends group", "Elderly parents", "Business trip"),
            key = PreferenceKeys.COMPANION
        )
    )

    // State for all preferences - using individual state for each category
    var travelStylePrefs by remember { mutableStateOf(emptySet<String>()) }
    var transportationPrefs by remember { mutableStateOf(emptySet<String>()) }
    var accommodationPrefs by remember { mutableStateOf(emptySet<String>()) }
    var dietaryPrefs by remember { mutableStateOf(emptySet<String>()) }
    var pacePrefs by remember { mutableStateOf(emptySet<String>()) }
    var activitiesPrefs by remember { mutableStateOf(emptySet<String>()) }
    var durationPrefs by remember { mutableStateOf(emptySet<String>()) }
    var climatePrefs by remember { mutableStateOf(emptySet<String>()) }
    var safetyPrefs by remember { mutableStateOf(emptySet<String>()) }
    var companionPrefs by remember { mutableStateOf(emptySet<String>()) }

    // Helper function to get current preferences for a category
    fun getCurrentPreferences(key: Preferences.Key<Set<String>>): Set<String> {
        return when (key) {
            PreferenceKeys.TRAVEL_STYLE -> travelStylePrefs
            PreferenceKeys.TRANSPORTATION -> transportationPrefs
            PreferenceKeys.ACCOMMODATION -> accommodationPrefs
            PreferenceKeys.DIETARY -> dietaryPrefs
            PreferenceKeys.PACE -> pacePrefs
            PreferenceKeys.ACTIVITIES -> activitiesPrefs
            PreferenceKeys.DURATION -> durationPrefs
            PreferenceKeys.CLIMATE -> climatePrefs
            PreferenceKeys.SAFETY -> safetyPrefs
            PreferenceKeys.COMPANION -> companionPrefs
            else -> emptySet()
        }
    }
    

    // Helper function to update preferences state
    fun updatePreferencesState(key: Preferences.Key<Set<String>>, value: Set<String>) {
        when (key) {
            PreferenceKeys.TRAVEL_STYLE -> travelStylePrefs = value
            PreferenceKeys.TRANSPORTATION -> transportationPrefs = value
            PreferenceKeys.ACCOMMODATION -> accommodationPrefs = value
            PreferenceKeys.DIETARY -> dietaryPrefs = value
            PreferenceKeys.PACE -> pacePrefs = value
            PreferenceKeys.ACTIVITIES -> activitiesPrefs = value
            PreferenceKeys.DURATION -> durationPrefs = value
            PreferenceKeys.CLIMATE -> climatePrefs = value
            PreferenceKeys.SAFETY -> safetyPrefs = value
            PreferenceKeys.COMPANION -> companionPrefs = value
        }
    }

    // Load preferences from DataStore - Load each category separately
    LaunchedEffect(Unit) {
        categories.forEach { category ->
            launch {
                context.dataStore.data
                    .map { preferences -> preferences[category.key] ?: emptySet<String>() }
                    .collect { savedPreferences ->
                        updatePreferencesState(category.key, savedPreferences)
                    }
            }
        }
    }

    // Function to save preferences to DataStore
    fun savePreference(key: Preferences.Key<Set<String>>, value: Set<String>) {
        scope.launch {
            context.dataStore.edit { preferences ->
                preferences[key] = value
            }
            updatePreferencesState(key, value)
        }
    }

    // Function to toggle chip selection - Always allow multiple selections
    fun toggleChip(category: PreferenceCategory, option: String) {
        val currentSelection = getCurrentPreferences(category.key)
        val newSelection = if (currentSelection.contains(option)) {
            currentSelection - option
        } else {
            currentSelection + option
        }
        savePreference(category.key, newSelection)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "Travel Preferences",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp, start = 16.dp, top = 16.dp)
            )
        }

        items(categories) { category ->
            PreferenceCategorySection(
                category = category,
                selectedOptions = getCurrentPreferences(category.key),
                onToggleOption = { option -> toggleChip(category, option) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))

            // Summary of all selected preferences
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📋 Selected Preferences Summary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    categories.forEach { category ->
                        val selected = getCurrentPreferences(category.key)
                        if (selected.isNotEmpty()) {
                            Text(
                                text = "${category.emoji} ${category.title}: ${selected.joinToString(", ")}",
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 2.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Show message if no preferences selected
                    val totalSelected = categories.sumOf { getCurrentPreferences(it.key).size }
                    if (totalSelected == 0) {
                        Text(
                            text = "No preferences selected yet. Start selecting your travel preferences above!",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun PreferenceCategorySection(
    category: PreferenceCategory,
    selectedOptions: Set<String>,
    onToggleOption: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${category.emoji} ${category.title}",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 16.dp)
            )

            // Show count of selected options
            if (selectedOptions.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.padding(start = 8.dp, end=16.dp)
                ) {
                    Text(
                        text = "${selectedOptions.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(category.options) { option ->
                PreferenceChip(
                    text = option,
                    isSelected = selectedOptions.contains(option),
                    onClick = { onToggleOption(option) }
                )
            }
        }
    }
}

@Composable
fun PreferenceChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        },
        selected = isSelected,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            selectedBorderColor = MaterialTheme.colorScheme.primary,
            borderColor = MaterialTheme.colorScheme.outline,
            selectedBorderWidth = 2.dp,
            borderWidth = 1.dp
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(horizontal = 2.dp)
    )
}