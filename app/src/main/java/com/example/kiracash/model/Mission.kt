package com.example.kiracash.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missions")
data class Mission(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val xpReward: Int,
    var isCompleted: Boolean = false // This field will be saved in the database
)