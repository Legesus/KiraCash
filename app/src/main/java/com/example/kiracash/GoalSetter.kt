package com.example.kiracash

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class Goal(
    val id: Int,
    var title: String,
    var amountGoal: Double,
    var amountSaved: Double
)

val sampleGoals = mutableListOf(
    Goal(1, "New Laptop", 1000.0, 450.0),
    Goal(2, "Vacation", 3000.0, 850.0),
    Goal(3, "Emergency Fund", 5000.0, 1200.0)
)

@Composable
fun GoalItem(goal: Goal, onEdit: (Goal) -> Unit, onDelete: (Goal) -> Unit) {
    val progress = (goal.amountSaved / goal.amountGoal).toFloat() // Ensure division results in a float
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(2.dp) // Use CardDefaults.elevatedCardElevation with smaller elevation
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = goal.title, fontWeight = FontWeight.Bold, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Goal: RM ${goal.amountGoal}", maxLines = 1)
                Text(text = "Saved: RM ${goal.amountSaved}", maxLines = 1)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp), // Smaller height for progress indicator
                )
            }
            Row(modifier = Modifier.wrapContentWidth()) {
                IconButton(onClick = { onEdit(goal) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { onDelete(goal) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun GoalList(goals: MutableList<Goal>) {
    var showDialog by remember { mutableStateOf(false) }
    var newGoalTitle by remember { mutableStateOf("") }
    var newGoalAmount by remember { mutableStateOf("") }
    var editingGoal by remember { mutableStateOf<Goal?>(null) }

    LazyColumn {
        items(goals) { goal ->
            GoalItem(
                goal = goal,
                onEdit = { goalToEdit ->
                    editingGoal = goalToEdit
                    newGoalTitle = goalToEdit.title
                    newGoalAmount = goalToEdit.amountGoal.toString()
                    showDialog = true
                },
                onDelete = { goalToDelete ->
                    goals.remove(goalToDelete)
                }
            )
        }

        item {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Add New Budget")
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (editingGoal != null) "Edit Budget" else "Add New Budget") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newGoalTitle,
                        onValueChange = { newGoalTitle = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newGoalAmount,
                        onValueChange = { newGoalAmount = it },
                        label = { Text("Goal Amount") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newGoalTitle.isNotBlank() && newGoalAmount.isNotBlank()) {
                            val goalAmount = newGoalAmount.toDoubleOrNull()
                            if (goalAmount != null) {
                                if (editingGoal != null) {
                                    val index = goals.indexOfFirst { it.id == editingGoal!!.id }
                                    if (index != -1) {
                                        goals[index] = editingGoal!!.copy(
                                            title = newGoalTitle,
                                            amountGoal = goalAmount
                                        )
                                    }
                                    editingGoal = null
                                } else {
                                    goals.add(
                                        Goal(
                                            id = goals.size + 1,
                                            title = newGoalTitle,
                                            amountGoal = goalAmount,
                                            amountSaved = 0.0
                                        )
                                    )
                                }
                                showDialog = false
                                newGoalTitle = ""
                                newGoalAmount = ""
                            }
                        }
                    }
                ) {
                    Text(if (editingGoal != null) "Save" else "Add")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGoalList() {
    GoalList(goals = sampleGoals)
}
