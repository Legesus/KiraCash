package com.example.kiracash.model

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ReceiptDao {
    @Transaction
    @Query("SELECT * FROM items INNER JOIN receipt_item_join ON items.id=receipt_item_join.itemId WHERE receipt_item_join.receiptId=:receiptId")
    suspend fun getItemsForReceipt(receiptId: Int): List<Item>
}