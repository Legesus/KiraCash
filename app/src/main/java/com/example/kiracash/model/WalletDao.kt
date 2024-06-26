package com.example.kiracash.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Transaction
    @Query("SELECT * FROM items INNER JOIN wallet_item_join ON items.id=wallet_item_join.itemId WHERE wallet_item_join.walletId=:walletId")
    fun getItemsForWallet(walletId: Int): Flow<List<Item>>

    @Query("SELECT * FROM wallets")
    fun getAllWallets(): Flow<List<Wallet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wallet: Wallet)

    @Update
    suspend fun update(wallet: Wallet)

    @Delete
    suspend fun delete(wallet: Wallet)

    @Query("""
    SELECT wallets.id, wallets.owner, IFNULL(SUM(paid_items.price), 0) as amountPaid, wallets.amountOwe, wallets.walletPicture, wallets.walletColor
    FROM wallets
    LEFT JOIN wallet_item_join ON wallets.id = wallet_item_join.walletId
    LEFT JOIN paid_items ON wallet_item_join.itemId = paid_items.id AND paid_items.isPaid = 1
    GROUP BY wallets.id
    """)
    fun getWalletsWithTotalAmountPaid(): Flow<List<Wallet>>

    @Query("""
    SELECT wallets.id, wallets.owner, wallets.amountPaid, IFNULL(SUM(paid_items.price), 0) as amountOwe, wallets.walletPicture, wallets.walletColor
    FROM wallets
    LEFT JOIN wallet_item_join ON wallets.id = wallet_item_join.walletId
    LEFT JOIN paid_items ON wallet_item_join.itemId = paid_items.id AND paid_items.isPaid = 0
    GROUP BY wallets.id
    """)
    fun getWalletsWithTotalAmountOwe(): Flow<List<Wallet>>

    @Query("SELECT id FROM wallets WHERE owner = :walletOwner")
    fun getWalletIdByOwner(walletOwner: String): Flow<Int>

    @Query("SELECT * FROM wallets WHERE owner = :walletOwner")
    fun getWalletByOwner(walletOwner: String): Flow<Wallet>
}
