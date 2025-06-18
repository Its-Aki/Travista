package com.example.travista.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TopDestinationsEntity::class], // Add all your Room entities here
    version = 1,
    exportSchema = false
)
// we use abstract here because RoomDatabase is an abstract class, and we need to override the abstract function.
// using class will give error by avoiding object creation and directly overriding .

abstract class TravistaDatabase : RoomDatabase() {

    // DAO provider function
    abstract fun topDestinationDao(): TopDestinationsDao
}