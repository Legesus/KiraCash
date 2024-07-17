package com.example.kiracash.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalSetDao {
    @Query("SELECT * FROM goal_set")
    fun getAllGoals(): Flow<List<GoalSet>>

    @Insert
    suspend fun insertGoal(goalSet: GoalSet)

    @Delete
    suspend fun deleteGoal(goalSet: GoalSet)

    @Update
    suspend fun updateGoal(goalSet: GoalSet)
}