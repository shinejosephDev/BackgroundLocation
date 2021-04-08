package com.sample.backgroundlocation.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationDao {
    @Query("SELECT * FROM location_data")
    suspend fun getAll(id: Long): LocationEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(director: LocationEntity): Long
}