package com.example.kiracash.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface WalletItemJoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(join: WalletItemJoin)
}