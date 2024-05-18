package com.example.kiracash.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReceiptItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(receiptItem: ReceiptItem)

    @Query("SELECT * FROM receipt_items")
    fun getReceiptItems(): LiveData<List<ReceiptItem>>

}