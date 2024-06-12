package com.example.kiracash.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PaidItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(paidItem: PaidItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(paidItems: List<PaidItem>)


    @Query("SELECT * FROM paid_items")
    fun getAllPaidItems(): Flow<List<PaidItem>>

    @Query("SELECT * FROM paid_items WHERE isPaid = 1")
    fun getPaidItems(): Flow<List<PaidItem>>

    @Query("SELECT * FROM paid_items WHERE isPaid = 0")
    fun getUnpaidItems(): Flow<List<PaidItem>>

    @Query("UPDATE paid_items SET isPaid = 1 WHERE id = :itemId")
    suspend fun markItemAsPaid(itemId: Int)

    @Query("UPDATE paid_items SET isPaid = 0 WHERE id = :itemId")
    suspend fun markItemAsUnpaid(itemId: Int)
}
