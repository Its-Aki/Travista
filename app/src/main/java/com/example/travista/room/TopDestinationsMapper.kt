package com.example.travista.room

import com.example.travista.data.topdestionations.TopDestinations

// Map from API model to Room entity
fun TopDestinations.toEntity(): TopDestinationsEntity {
    return TopDestinationsEntity(
        placeId = placeId ?: "",
        name = name ?: "Unknown",
        address = address ?: "Not Available",
        photoUrl = photoUrl, // ✅ imageUrl is now a String in the data class
        timestamp = System.currentTimeMillis()
    )
}


// Map a list of API models to a list of Room entities
fun List<TopDestinations>.toEntityList(): List<TopDestinationsEntity> {
    return this.mapNotNull { it.toEntity() }
}

// Optional: Map from Room entity to API model (if needed)
/*
fun TopDestinationsEntity.toTopDestination(): TopDestinations {
    return TopDestinations(
        placeId = placeId,
        name = name,
        address = address,
        photoUrl = photoUrl, // ✅ Preserve stored image URL
        photos = emptyList() // Photos list not used in Room
    )
}

// Optional: Map list of Room entities to list of API models
fun List<TopDestinationsEntity>.toTopDestinations(): List<TopDestinations> {
    return this.map { it.toTopDestination() }
}

 */
