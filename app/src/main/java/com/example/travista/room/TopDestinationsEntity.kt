package com.example.travista.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "top_destinations")
data class TopDestinationsEntity(
    @PrimaryKey val placeId: String,
    val name: String,
    val address: String,
    val photoUrl: String,
    val timestamp: Long // to check if it’s older than 24 hours
)
