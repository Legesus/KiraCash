package com.example.kiracash.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "wallet_item_join",
    primaryKeys = ["walletId", "itemId"],
    foreignKeys = [
        ForeignKey(
            entity = Wallet::class,
            parentColumns = ["id"],
            childColumns = ["walletId"]
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["itemId"]
        )
    ]
)
data class WalletItemJoin(
    val walletId: Int,
    val itemId: Int
)