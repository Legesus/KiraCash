package com.example.kiracash.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ReceiptItem::class, WalletEntity::class, WalletItemJoin::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun receiptItemDao(): ReceiptItemDao
    abstract fun walletDao(): WalletDao
    abstract fun walletItemJoinDao(): WalletItemJoinDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) { instance ?: buildDatabase(context).also { instance = it } }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "kiracash-db").fallbackToDestructiveMigration().build()
    }
}