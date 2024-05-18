package com.example.kiracash.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receipt_items")
data class ReceiptItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemName: String,
    val itemPrice: Double
)