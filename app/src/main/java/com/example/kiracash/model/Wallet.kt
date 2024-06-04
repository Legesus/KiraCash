package com.example.kiracash.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallets")
data class Wallet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val owner: String,
    val amountPaid: Double,
    val amountOwe: Double
)