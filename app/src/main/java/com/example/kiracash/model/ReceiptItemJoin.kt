package com.example.kiracash.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "receipt_item_join",
    primaryKeys = ["receiptId", "itemId"],
    foreignKeys = [
        ForeignKey(
            entity = Receipt::class,
            parentColumns = ["id"],
            childColumns = ["receiptId"]
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["itemId"]
        )
    ],
    indices = [Index(value = ["itemId"])] // Add this line

)
data class ReceiptItemJoin(
    val receiptId: Int,
    val itemId: Int
)
