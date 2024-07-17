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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kiracash.model.AppDatabase
import com.example.kiracash.model.GoalSet
import kotlinx.coroutines.launch

@Composable
fun GoalSetterScreen() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val goalSetDao = db.goalSetDao()
    val coroutineScope = rememberCoroutineScope()

    var goalsList by remember { mutableStateOf(listOf<GoalSet>()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            goalSetDao.getAllGoals().collect { goals ->
                goalsList = goals
            }
        }
    }

    GoalList(goals = goalsList, onGoalUpdated = { goal ->
        coroutineScope.launch {
            goalSetDao.updateGoal(goal)
        }
    }, onGoalDeleted = { goal ->
        coroutineScope.launch {
            goalSetDao.deleteGoal(goal)
        }
    }, onXPChange = { xpChange ->
        // Handle XP change, e.g., update a total XP state or call a function to update XP in the database
    })
}

@Composable
// Adjusted GoalList with XP change handler
fun GoalList(goals: List<GoalSet>, onGoalUpdated: (GoalSet) -> Unit, onGoalDeleted: (GoalSet) -> Unit, onXPChange: (Int) -> Unit) {
    LazyColumn {
        items(goals) { goal ->
            GoalItem(goal = goal, onEdit = onGoalUpdated, onDelete = onGoalDeleted, onXPChange = onXPChange)
        }
    }
}

@Composable
fun GoalItem(goal: GoalSet, onEdit: (GoalSet) -> Unit, onDelete: (GoalSet) -> Unit, onXPChange: (Int) -> Unit) {
    val progress = (goal.amountSaved / goal.amountGoal).toFloat()
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = goal.title, fontWeight = FontWeight.Bold, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Goal: RM ${goal.amountGoal}", maxLines = 1)
                Text(text = "Saved: RM ${goal.amountSaved}", maxLines = 1)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                )
            }
            Row(modifier = Modifier.wrapContentWidth()) {
                IconButton(onClick = { /* Handle edit */ }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { onDelete(goal); onXPChange(-5) }) { // Example XP change on delete
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }

        // Check if the goal is reached and call onXPChange with 100
        if (goal.isReached && goal.amountSaved >= goal.amountGoal) {
            onXPChange(100)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGoalSetterScreen() {
    GoalSetterScreen()
}