package com.example.kiracash

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kiracash.model.AppDatabase
import com.example.kiracash.model.Mission
import com.example.kiracash.model.XPEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


enum class BudgetTabs(val title: String) {
    PixelPlant("Pixel Plant"),
    Missions("Missions"),
    Goals("Goals")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val missionDao = db.missionDao()
    val goalSetDao = db.goalSetDao()
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(BudgetTabs.PixelPlant) }

    // Sample Missions data
    val initialMissions = listOf(
        Mission(
            title = "Save Daily",
            description = "Put aside at least 5% of your daily earnings.",
            xpReward = 15,
            isCompleted = false
        ),
        Mission(
            title = "Limit Eating Out",
            description = "Try not to eat out more than once today.",
            xpReward = 10,
            isCompleted = false
        ),
        Mission(
            title = "Track Spending",
            description = "Record every expense you make today.",
            xpReward = 8,
            isCompleted = false
        )
    )

    // This is the correct place to collect goals from the database
    val goalsList by goalSetDao.getAllGoals().collectAsState(initial = emptyList())

    // Pixel Plant Data
    val samplePlantName = "Green Buddy"
    val xpHistory = remember { mutableStateListOf<XPEntry>() }
    var totalXP by remember { mutableIntStateOf(calculateTotalXP(xpHistory)) }

    // Observe mission completion changes
    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) { // Use launch directly within LaunchedEffect
            missionDao.getAllMissionsFlow().collect { missions ->
                missions.forEach { mission ->
                    if (mission.isCompleted) {
                        val existingEntry = xpHistory.find { it.source == mission.title }
                        if (existingEntry == null) {
                            xpHistory.add(XPEntry(mission.title, mission.xpReward))
                            totalXP += mission.xpReward
                        }
                    } else { // Check if mission is no longer completed
                        val existingEntry = xpHistory.find { it.source == mission.title }
                        if (existingEntry != null) {
                            xpHistory.remove(existingEntry)
                            totalXP -= mission.xpReward
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Budget Menu",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1C1B22)
                )
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TabRow(selectedTabIndex = selectedTab.ordinal) {
                BudgetTabs.entries.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab.title) }
                    )
                }
            }
            when (selectedTab) {
                BudgetTabs.PixelPlant ->
                    PixelPlant(
                        initialProgress = totalXP / 350.0,
                        plantName = samplePlantName,
                        xpHistory = xpHistory
                    )

                BudgetTabs.Missions ->
                    MissionListWithSwitches(missions = initialMissions) { xpChange ->
                        totalXP += xpChange
                    }
                // Corrected GoalList call
                BudgetTabs.Goals ->
                    GoalList(goalSetDao, goals = goalsList, onGoalUpdated = { goal ->
                        coroutineScope.launch {
                            updateGoal(goalSetDao, goal, goal.amountSaved) { xpChange ->
                                totalXP += xpChange
                                xpHistory.add(XPEntry("Goal Reached/Unreached", xpChange))
                            }
                        }
                    }, onGoalDeleted = { goal ->
                        coroutineScope.launch {
                            goalSetDao.deleteGoal(goal)
                        }
                    }, onXPChange = { xpChange ->
                        totalXP += xpChange
                        xpHistory.add(XPEntry("Goal Reached/Unreached", xpChange))
                    })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBudgetScreen() {
    val mockNavController = rememberNavController()
    BudgetScreen(navController = mockNavController)
}