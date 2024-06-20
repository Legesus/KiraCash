package com.example.kiracash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kiracash.model.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val DarkRed = Color(0xFF8B0000)  // Custom dark red color
val DarkGreen = Color(0xFF006400)  // Custom dark green color

@Composable
fun CardItem(title: String, cash: Float, isDebtPositive: Boolean = false) {
    val textColor = when (title) {
        "Owe Them" -> DarkRed
        "Owe You" -> DarkGreen
        "Expenses" -> Color.Red
        "Income" -> Color.Green
        "Total" -> if (isDebtPositive) Color.Green else Color.Red
        else -> MaterialTheme.colorScheme.onBackground
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(10.dp),
        backgroundColor = MaterialTheme.colorScheme.inverseOnSurface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(100.dp)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "RM${String.format("%.2f", cash)}",
                color = textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview
@Composable
fun CardSection() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val walletDao = db.walletDao()
    val paidItemDao = db.paidItemDao()
    val coroutineScope = rememberCoroutineScope()

    // State to hold the sum of amountPaid, amountOwe and totalBudget
    var amountPaid by remember { mutableStateOf(0f) }
    var amountOwe by remember { mutableStateOf(0f) }
    var totalBudget by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            val paidItems = paidItemDao.getAllPaidItems().first()
            amountPaid = paidItems.sumOf { it.price }.toFloat() // Correctly sum the prices
            amountOwe = paidItems.sumOf { if (!it.isPaid) it.price else 0.0 }.toFloat()
            totalBudget = amountPaid - amountOwe
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        item { CardItem("Income", 0f) }
        item { CardItem("Expenses", 0f) }
        item { CardItem("Owe You", amountPaid) }
        item { CardItem("Owe Them", amountOwe) }
        item { CardItem("Total", totalBudget) }
    }
}
