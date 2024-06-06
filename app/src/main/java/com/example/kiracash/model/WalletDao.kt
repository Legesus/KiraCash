package com.example.kiracash.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface WalletDao {
    @Transaction
    @Query("SELECT * FROM items INNER JOIN wallet_item_join ON items.id=wallet_item_join.itemId WHERE wallet_item_join.walletId=:walletId")
    suspend fun getItemsForWallet(walletId: Int): List<Item>

    @Query("SELECT * FROM wallets")
    suspend fun getAllWallets(): List<Wallet>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wallet: Wallet)

    @Query("""
        SELECT wallets.id, wallets.owner, SUM(items.price) as amountPaid, wallets.amountOwe 
        FROM wallets 
        INNER JOIN wallet_item_join ON wallets.id = wallet_item_join.walletId 
        INNER JOIN items ON wallet_item_join.itemId = items.id 
        GROUP BY wallets.id
    """)
    suspend fun getWalletsWithTotalAmountPaid(): List<Wallet>
}