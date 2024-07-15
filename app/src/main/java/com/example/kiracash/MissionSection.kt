package com.example.kiracash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kiracash.model.AppDatabase
import com.example.kiracash.model.Mission
import com.example.kiracash.model.MissionDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun MissionItemWithSwitch(mission: Mission, onMissionCompletionChange: (Mission, Boolean, Int) -> Unit, onTotalXPChange: (Int) -> Unit) {
    val cardModifier = Modifier
        .padding(6.dp)
        .fillMaxWidth()

    val cardColor = if (mission.isCompleted) Color(0xFF509BFF) else MaterialTheme.colorScheme.surface

    Card(
        modifier = cardModifier,
        elevation = CardDefaults.elevatedCardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = mission.title, style = MaterialTheme.typography.titleLarge)
            Text(text = mission.description, style = MaterialTheme.typography.bodyMedium)
            if (mission.isCompleted) {
                Text(text = "XP: ${mission.xpReward}", style = MaterialTheme.typography.bodyLarge)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (mission.isCompleted) {
                    Text(text = "Completed", color = Color.Green)
                } else {
                    Text(text = "Incomplete", color = Color.Red)
                }
                Switch(
                    checked = mission.isCompleted,
                    onCheckedChange = { isChecked ->
                        onMissionCompletionChange(mission, isChecked, mission.xpReward)
                        if (isChecked) {
                            onTotalXPChange(mission.xpReward)
                        } else {
                            onTotalXPChange(-mission.xpReward)
                        }
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Green)
                )
            }
        }
    }
}

@Composable
fun MissionListWithSwitches(missions: List<Mission>, onTotalXPChange: (Int) -> Unit) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val missionDao = db.missionDao()
    val coroutineScope = rememberCoroutineScope()

    var timeUntilReset by remember { mutableStateOf(getTimeUntilNextReset()) }

    // Observe missions from the database
    val missionList by missionDao.getAllMissionsFlow().collectAsState(initial = emptyList())

    // Total duration for the countdown in milliseconds (e.g., 24 hours)
    val totalDuration = TimeUnit.HOURS.toMillis(24)

    // Calculate the progress for the countdown
    val progress = remember(timeUntilReset) { (totalDuration - timeUntilReset).toFloat() / totalDuration }

    // Reset missions at midnight
    LaunchedEffect(Unit) {
        while (true) {
            delay(timeUntilReset)
            coroutineScope.launch(Dispatchers.IO) {
                missionDao.resetMissions()
            }
            timeUntilReset = getTimeUntilNextReset()
        }
    }

    // Update timeUntilReset every second
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) // Use 1000 milliseconds instead of 1 second
            timeUntilReset -= 1000
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    var newMissionTitle by remember { mutableStateOf("") }
    var newMissionDescription by remember { mutableStateOf("") }
    var newMissionXP by remember { mutableStateOf("") }
    var editingMission by remember { mutableStateOf<Mission?>(null) }

    Column {
        // Add spacer
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Time Until Reset: ${formatTime(timeUntilReset)}", color = Color.White) // Display countdown
        // Display the progress bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.padding(16.dp),
        )
        LazyColumn {
            items(missionList) { mission ->
                MissionItemWithSwitch(
                    mission = mission,
                    onMissionCompletionChange = { updatedMission, isChecked, _ ->
                        coroutineScope.launch(Dispatchers.IO) {
                            missionDao.updateMission(updatedMission.copy(isCompleted = isChecked))
                        }
                    },
                    onTotalXPChange = onTotalXPChange
                )
            }

            item {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Add New Mission")
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (editingMission != null) "Edit Mission" else "Add New Mission") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newMissionTitle,
                        onValueChange = { newMissionTitle = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newMissionDescription,
                        onValueChange = { newMissionDescription = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newMissionXP,
                        onValueChange = { newMissionXP = it },
                        label = { Text("XP Reward") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newMissionTitle.isNotBlank() && newMissionDescription.isNotBlank() && newMissionXP.isNotBlank()) {
                            val xpReward = newMissionXP.toIntOrNull()
                            if (xpReward != null) {
                                if (editingMission != null) {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        missionDao.updateMission(
                                            editingMission!!.copy(
                                                title = newMissionTitle,
                                                description = newMissionDescription,
                                                xpReward = xpReward
                                            )
                                        )
                                    }
                                    editingMission = null
                                } else {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        missionDao.insertMission(
                                            Mission(
                                                title = newMissionTitle,
                                                description = newMissionDescription,
                                                xpReward = xpReward
                                            )
                                        )
                                    }
                                }
                                showDialog = false
                                newMissionTitle = ""
                                newMissionDescription = ""
                                newMissionXP = ""
                            }
                        }
                    }
                ) {
                    Text(if (editingMission != null) "Save" else "Add")
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

// Calculate time until next midnight
fun getTimeUntilNextReset(): Long {
    val now = Calendar.getInstance()
    val midnight = Calendar.getInstance().apply {
        timeInMillis = now.timeInMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (before(now)) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }
    return midnight.timeInMillis - now.timeInMillis
}

// Format time for display
fun formatTime(timeInMillis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(timeInMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
}

// Add resetMissions function to MissionDao
fun MissionDao.resetMissions() {
    updateMissionCompletionStatus(false)
}

@Preview(showBackground = true)
@Composable
fun PreviewMissionList() {
    val sampleMissions = listOf(
        Mission(title = "Save Daily", description = "Put aside at least 5% of your daily earnings.", xpReward = 15, isCompleted = false),
        Mission(title = "Limit Eating Out", description = "Try not to eat out more than once today.", xpReward = 10, isCompleted = false),
        Mission(title = "Track Spending", description = "Record every expense you make today.", xpReward = 8, isCompleted = false)
    )
    MissionListWithSwitches(missions = sampleMissions) {}
}