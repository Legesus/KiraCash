package com.example.kiracash.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(personalItem: PersonalItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(personalItems: List<PersonalItem>)

    @Query("SELECT * FROM personal_items WHERE walletId = :walletId")
    fun getPersonalItemsByWalletId(walletId: Int): Flow<List<PersonalItem>>

    @Query("SELECT * FROM personal_items WHERE category = :category AND walletId = :walletId")
    fun getPersonalItemsByCategory(category: String, walletId: Int): Flow<List<PersonalItem>>
    // Add other queries as needed

    @Query("SELECT * FROM personal_items")
    fun getAllPersonalItems(): Flow<List<PersonalItem>>

    @Insert
    fun insertPersonalItem(personalItem: PersonalItem)

    @Query("SELECT * FROM personal_items WHERE dateExpense = :date")
    fun getPersonalItemsByDate(date: String): Flow<List<PersonalItem>>

    @Query("SELECT DISTINCT category FROM personal_items")
    fun getCategories(): Flow<List<String>>

    @Query("""
    SELECT 
        strftime('%Y-%m', dateExpense) as month, 
        category,
        SUM(price) as total 
    FROM personal_items 
    GROUP BY month, category
    """)
    fun getMonthlyCategoryExpenses(): Flow<List<MonthlyCategoryExpense>>
}