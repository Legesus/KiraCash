package com.example.kiracash

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.kiracash.model.GoalSet
import com.example.kiracash.model.Item

// SharedViewModel.kt (or similar shared state mechanism)
class SharedViewModel : ViewModel() {
    val extractedItems = mutableStateOf<List<Item>>(emptyList())
    val showReceiptDialog = mutableStateOf(false)

    // Properties for the goal saving dialog
    val showGoalDialog = mutableStateOf(false)
    val selectedGoal = mutableStateOf<GoalSet?>(null)
    val amountToSave = mutableStateOf("")
}