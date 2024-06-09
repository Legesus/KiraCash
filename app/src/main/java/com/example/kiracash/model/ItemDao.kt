package com.example.kiracash.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item): Long  // Make insert return the id

    @Insert
    suspend fun insertAll(items: List<Item>)

    @Query("SELECT * FROM items")
    suspend fun getAll(): List<Item>

    @Transaction
    @Query("SELECT * FROM receipts INNER JOIN receipt_item_join ON receipts.id=receipt_item_join.receiptId WHERE receipt_item_join.itemId=:itemId")
    suspend fun getReceiptsForItem(itemId: Int): List<Receipt>

    @Transaction
    @Query("SELECT * FROM wallets INNER JOIN wallet_item_join ON wallets.id=wallet_item_join.walletId WHERE wallet_item_join.itemId=:itemId")
    suspend fun getWalletsForItem(itemId: Int): List<Wallet>
}
