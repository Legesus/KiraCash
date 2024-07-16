package com.example.kiracash.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "personal_items",
    foreignKeys = [
        ForeignKey(
            entity = Wallet::class,
            parentColumns = ["id"],
            childColumns = ["walletId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PersonalItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val category: String, // Add category
    val walletId: Int, // Add walletId
    val dateExpense: String // Add this line
)

data class MonthlyCategoryExpense(
    val month: String,
    val category: String,
    val total: Double
)