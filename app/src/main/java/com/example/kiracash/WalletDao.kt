package com.example.kiracash.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WalletDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wallet: WalletEntity)

    @Query("SELECT * FROM WalletEntity")
    fun getWallets(): LiveData<List<WalletEntity>>
}