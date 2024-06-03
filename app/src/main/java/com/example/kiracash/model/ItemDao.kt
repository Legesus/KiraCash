package com.example.kiracash.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDao {
    @Insert
    suspend fun insertAll(items: List<Item>)

    @Query("SELECT * FROM items")
    suspend fun getAll(): List<Item>
}
