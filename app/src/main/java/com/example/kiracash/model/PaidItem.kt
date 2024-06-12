package com.example.kiracash.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paid_items")
data class PaidItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val isPaid: Boolean,
    val walletId: Int
)
