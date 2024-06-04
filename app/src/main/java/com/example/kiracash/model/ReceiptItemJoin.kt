package com.example.kiracash.model

import androidx.room.Entity
import androidx.room.ForeignKey

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
    ]
)
data class ReceiptItemJoin(
    val receiptId: Int,
    val itemId: Int
)
