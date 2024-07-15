package com.example.kiracash.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {
    @Query("SELECT * FROM missions")
    fun getAllMissions(): List<Mission>

    @Insert
    fun insertMission(mission: Mission)

    @Update
    fun updateMission(mission: Mission)

    @Query("SELECT SUM(xpReward) FROM missions WHERE isCompleted = 1")
    fun getTotalXPEarned(): Int

    @Query("SELECT * FROM missions")
    fun getAllMissionsFlow(): Flow<List<Mission>>

    @Query("UPDATE missions SET isCompleted = :isCompleted")
    fun updateMissionCompletionStatus(isCompleted: Boolean)
}