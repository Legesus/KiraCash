package com.example.kiracash.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WalletEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val walletName: String
)