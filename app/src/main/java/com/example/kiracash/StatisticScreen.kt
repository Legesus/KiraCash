package com.example.kiracash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class StatisticScreen : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            StatisticScreen(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Statistic Menu", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1C1B22)
                )
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF1C1B24)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title and Chart
            Text(
                text = "Statistic",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            // Replace with actual chart implementation
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.LightGray)
            )
            // Item List
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                // Replace with dynamic data and styling
                Text(
                    text = "Item #1",
                    color = Color(0xFFF04E5F),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Item #1",
                    color = Color(0xFF8E54E9),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Item #1",
                    color = Color(0xFF39A2DB),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Item #1",
                    color = Color(0xFF3BDA92),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Item #1",
                    color = Color(0xFFFF9800),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticScreenPreview() {
    val mockNavController = rememberNavController()
    StatisticScreen(navController = mockNavController)
}