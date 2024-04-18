package com.example.kiracash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class DebtMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            DebtMenuScreen(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtMenuScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Wallet", color = Color.White) },
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFF1C1B22))
                .padding(16.dp)
        ) {
            // Dropdown for user selection
            Dropdown(items = listOf("Edward", "User 2", "User 3"))

            Spacer(modifier = Modifier.height(16.dp))

            // Section title with options menu
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Breakfast",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Options", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // List of items with checkboxes
            ItemsList()
        }
    }
}

@Composable
fun Dropdown(items: List<String>) {
    var expanded = false
    var selectedItem = items[0] // Initial selection

    Box {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282731))
        ) {
            Text(text = selectedItem, color = Color.White)
            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown", tint = Color.White)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item, color = Color.White) },
                    onClick = {
                        selectedItem = item
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ItemsList() {
    val items = listOf("Item #1", "Item #2", "Item #3", "Item #4")
    items.forEach { item ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = item, color = Color.White)
            Spacer(modifier = Modifier.weight(1f))
            Checkbox(checked = true, onCheckedChange = {}, colors = CheckboxDefaults.colors(checkedColor = Color(0xFF509BFF)))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DebtMenuPreview() {
    val mockNavController = rememberNavController()
    DebtMenuScreen(navController = mockNavController)
}