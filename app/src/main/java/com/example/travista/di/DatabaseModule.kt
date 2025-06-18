package com.example.travista.di

import android.content.Context
import androidx.room.Room
import com.example.travista.room.TopDestinationsDao
import com.example.travista.room.TravistaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module to provide Room Database and DAO instances
 */
@Module
@InstallIn(SingletonComponent::class) // Available app-wide (singleton scope)
object DatabaseModule {

    /**
     * Provides a singleton instance of TravistaDatabase
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context
    ): TravistaDatabase {
        return Room.databaseBuilder(
            context,
            TravistaDatabase::class.java,
            "travista_db" // Name of your SQLite database file automatically generated
        ).build()
    }

    /**
     * Provides TopDestinationsDao from the database instance
     */
    @Provides
    fun provideTopDestinationsDao(
        database: TravistaDatabase
    ): TopDestinationsDao {
        return database.topDestinationDao()
    }
}
