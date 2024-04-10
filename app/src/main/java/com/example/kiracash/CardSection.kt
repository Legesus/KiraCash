package com.example.kiracash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CardItem(title: String, cash: Float, isDebtPositive: Boolean = false) {
    val textColor = when (title) {
        "Expenses" -> Color.Red
        "Income" -> Color(0xFF008000)
        "Debt" -> if (isDebtPositive) Color(0xFF008000) else Color.Red
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
            Box(modifier = Modifier.width(100.dp)){
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "RM$cash",
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
    // Add your content here
    Column {
        CardItem("Expenses", 24.0f)
        CardItem("Income", 25.0f)
        CardItem("Debt", 10.0f, isDebtPositive = true)
    }
}