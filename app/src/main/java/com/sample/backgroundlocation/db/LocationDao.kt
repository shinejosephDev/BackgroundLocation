package com.sample.backgroundlocation.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationDao {
    @Query("SELECT * FROM locationdata")
    suspend fun getAll(): List<LocationData>

    @Query("SELECT * FROM locationdata WHERE created_at BETWEEN :start AND :end")
    suspend fun fetchAllData(start: Long, end: Long): List<LocationData>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(director: LocationData): Long
}