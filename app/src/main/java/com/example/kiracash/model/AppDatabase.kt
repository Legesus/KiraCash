package com.example.kiracash.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@Database(entities = [Receipt::class, Item::class, Wallet::class, WalletItemJoin::class, ReceiptItemJoin::class, PaidItem::class, Mission::class, PersonalItem::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun receiptDao(): ReceiptDao
    abstract fun itemDao(): ItemDao
    abstract fun walletDao(): WalletDao
    abstract fun walletItemJoinDao(): WalletItemJoinDao
    abstract fun receiptItemJoinDao(): ReceiptItemJoinDao
    abstract fun paidItemDao(): PaidItemDao
    abstract fun missionDao(): MissionDao
    abstract fun personalItemDao(): PersonalItemDao // New DAO for Personal Items

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
                    .addCallback(DatabaseCallback(context))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database)
                    }
                }
            }

            private suspend fun populateDatabase(db: AppDatabase) {
                // Insert the sample data here
                val itemDao = db.itemDao()
                val receiptDao = db.receiptDao()
                val walletDao = db.walletDao()
                val receiptItemJoinDao = db.receiptItemJoinDao()
                val walletItemJoinDao = db.walletItemJoinDao()
                val paidItemDao = db.paidItemDao()
                val personalItemDao = db.personalItemDao() // Access the new DAO
                val missionDao = db.missionDao()

                // Delete all data in the database
                db.clearAllTables()

                val sampleMissions = listOf(
                    Mission(title = "Save Daily", description = "Put aside at least 5% of your daily earnings.", xpReward = 15, isCompleted = false),
                    Mission(title = "Limit Eating Out", description = "Try not to eat out more than once today.", xpReward = 10, isCompleted = false),
                    Mission(title = "Track Spending", description = "Record every expense you make today.", xpReward = 8, isCompleted = false)
                )
                sampleMissions.forEach { missionDao.insertMission(it) }

                // Create Wallet objects
                val wallets = listOf(
                    Wallet(owner = "Myself", amountPaid = 0.0, amountOwe = 0.0, walletPicture = "proficon", walletColor = 0xFFFF00),
                    Wallet(owner = "John Doe", amountPaid = 0.0, amountOwe = 0.0, walletPicture = "proficon", walletColor = 0xFF0000),
                    Wallet(owner = "Jane Doe", amountPaid = 0.0, amountOwe = 0.0, walletPicture = "proficon", walletColor = 0x00FF00),
                    Wallet(owner = "John Smith", amountPaid = 0.0, amountOwe = 0.0, walletPicture = "proficon", walletColor = 0x0000FF)
                )

                // Insert wallets into the database
                wallets.forEach { walletDao.insert(it) }

                // Retrieve the inserted wallets to get their IDs
                val insertedWallets = walletDao.getAllWallets().first()

                // Define initial dates for the items
                val initialDates = listOf("2023-01-15", "2023-02-15", "2023-03-15")

                // Create Item objects
                val items = listOf(
                    Item(name = "L3 12 LAKSA MEDIUM", price = 12.00),
                    Item(name = "L3 12 LAKSA MEDIUM", price = 12.00),
                    Item(name = "L3 12 LAKSA MEDIUM", price = 12.00),
                    Item(name = "L1 9 LAKSA NORMAL", price = 9.00),
                    Item(name = "L1 9 LAKSA NORMAL", price = 9.00),
                    Item(name = "TEA O PENG", price = 2.50),
                    Item(name = "TEA O PENG", price = 2.50),
                    Item(name = "TEA TARIK PENG", price = 3.20),
                    Item(name = "KOPI TARIK PENG", price = 3.20),
                    Item(name = "AIR SEJUK", price = 0.30)
                )

                // Insert items into the database
                itemDao.insertAll(items)

                // Create PaidItem objects with dates
                val paidItems = items.mapIndexed { index, item ->
                    val isPaid = index < items.size / 2
                    val walletId = insertedWallets[index % insertedWallets.size].id
                    val datePaid = initialDates[index % initialDates.size] // Cycle through the initialDates list
                    PaidItem(name = item.name, price = item.price, isPaid = isPaid, walletId = walletId, datePaid = datePaid)
                }

                // Insert paid items into the database
                paidItemDao.insertAll(paidItems)

                // Retrieve the inserted items to get their IDs
                val insertedItems = itemDao.getAll()

                // Create a single Receipt object
                val receipt = Receipt()

                // Insert the receipt into the database
                val receiptId = receiptDao.insert(receipt)

                // Create ReceiptItemJoin objects to link each item to the receipt
                val receiptItemJoins = insertedItems.map { item ->
                    ReceiptItemJoin(receiptId = receiptId.toInt(), itemId = item.id)
                }

                // Insert joins into the database
                receiptItemJoins.forEach { receiptItemJoinDao.insert(it) }

                // Create WalletItemJoin objects to link each item to its corresponding wallet
                val walletItemJoins = insertedItems.mapIndexed { index, item ->
                    WalletItemJoin(walletId = insertedWallets[index % insertedWallets.size].id, itemId = item.id)
                }

                // Insert joins into the database
                walletItemJoins.forEach { walletItemJoinDao.insert(it) }

                // Assuming "Myself" wallet is already created as shown in the existing populateDatabase method
                val myselfWalletId = walletDao.getWalletIdByOwner("Myself").firstOrNull() ?: return

                // Create PersonalItem objects with dates
                val personalItems = listOf(
                    PersonalItem(name = "Coffee", price = 5.0, category = "Food & Drink", walletId = myselfWalletId, dateExpense = "2023-01-20"),
                    PersonalItem(name = "Netflix Subscription", price = 9.99, category = "Entertainment", walletId = myselfWalletId, dateExpense = "2023-02-20"),
                    PersonalItem(name = "Gym Membership", price = 25.0, category = "Health & Fitness", walletId = myselfWalletId, dateExpense = "2023-03-20")
                )

                // Insert personal items into the database
                personalItemDao.insertAll(personalItems)
            }
        }
    }
}