package com.example.kiracash.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["walletId", "itemId"],
    foreignKeys = [
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["id"],
            childColumns = ["walletId"]
        ),
        ForeignKey(
            entity = ReceiptItem::class,
            parentColumns = ["id"],
            childColumns = ["itemId"]
        )
    ]
)
data class WalletItemJoin(
    val walletId: Long,
    val itemId: Long
)