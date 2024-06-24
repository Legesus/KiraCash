package com.example.kiracash.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface WalletItemJoinDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(join: WalletItemJoin)
}