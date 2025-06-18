package com.example.travista.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TopDestinationsDao {

    @Query("SELECT * FROM top_destinations")
    suspend fun getAllDestinations(): List<TopDestinationsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopDestinations(destinations: List<TopDestinationsEntity>)

    @Query("DELETE FROM top_destinations")
    suspend fun clearAll()
}
