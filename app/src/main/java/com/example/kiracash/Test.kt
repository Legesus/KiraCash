package com.example.kiracash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp

class Test : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF1C1B22)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            TopAppBar(
                title = { Text("Home", color = Color.White) },
                backgroundColor = Color(0xFF121117)
            )
            Spacer(modifier = Modifier.height(20.dp))
            CardItem("Expenses", 20.0f)
            CardItem("Income", 20.0f)
            CardItem("Debt", 20.0f, isDebtPositive = true)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Quick Actions",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            QuickActionsRow()
        }
    }
}




@Composable
fun BottomNavigationBar() {
    BottomNavigation(
        backgroundColor = Color(0xFF121117)
    ) {
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White) },
            selected = true,
            onClick = { /*TODO*/ },
            selectedContentColor = Color(0xFF6200EE),
            unselectedContentColor = Color.White
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Transactions", tint = Color.White) },
            selected = false,
            onClick = { /*TODO*/ },
            selectedContentColor = Color(0xFF6200EE),
            unselectedContentColor = Color.White
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Camera, contentDescription = "Scan", tint = Color.White) },
            selected = false,
            onClick = { /*TODO*/ },
            selectedContentColor = Color(0xFF6200EE),
            unselectedContentColor = Color.White
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    Icons.Default.TrendingUp,
                    contentDescription = "Insights",
                    tint = Color.White
                )
            },
            selected = false,
            onClick = { /*TODO*/ },
            selectedContentColor = Color(0xFF6200EE),
            unselectedContentColor = Color.White
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White) },
            selected = false,
            onClick = { /*TODO*/ },
            selectedContentColor = Color(0xFF6200EE),
            unselectedContentColor = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainScreen()
}