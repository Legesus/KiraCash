package com.example.kiracash

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
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

@Composable
fun CardItem(title: String, cash: Float, isDebtPositive: Boolean = false) {
    val textColor = when (title) {
        "Owe" -> Color.Red
        "Are Owed" -> Color(0xFF008000)
        "Total Budget" -> if (isDebtPositive) Color(0xFF008000) else Color.Red
        else -> MaterialTheme.colorScheme.onBackground
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
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

    Column {
        CardItem("Total Budget", totalBudget)
        CardItem("Are Owed", amountPaid)
        CardItem("Owe", amountOwe)
    }
}
