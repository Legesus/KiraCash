package com.example.kiracash.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Receipt::class, Item::class, Wallet::class, WalletItemJoin::class, ReceiptItemJoin::class, PaidItem::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun receiptDao(): ReceiptDao
    abstract fun itemDao(): ItemDao
    abstract fun walletDao(): WalletDao
    abstract fun walletItemJoinDao(): WalletItemJoinDao
    abstract fun receiptItemJoinDao(): ReceiptItemJoinDao
    abstract fun paidItemDao(): PaidItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}