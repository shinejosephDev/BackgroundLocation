package com.sample.backgroundlocation.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(director: User): Long

    @Query("SELECT id FROM user LIMIT 1")
    suspend fun getUUID(): String

    @Query("SELECT COUNT(id) FROM user")
     fun getAll(): Int

}