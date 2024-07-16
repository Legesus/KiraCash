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
}