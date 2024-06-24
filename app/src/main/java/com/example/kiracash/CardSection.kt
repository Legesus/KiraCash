package com.example.kiracash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kiracash.model.AppDatabase
import com.example.kiracash.model.Budget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val DarkRed = Color(0xFF8B0000)
val DarkGreen = Color(0xFF006400)

@Composable
fun TopUpDialog(onDismiss: () -> Unit, onTopUp: (Float) -> Unit) {
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Top Up Money", color = Color.White) },
        text = {
            Column {
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount", color = Color.White) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onTopUp(amount.toFloatOrNull() ?: 0f)
                    onDismiss()
                }
            ) {
                Text("Top Up", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}

@Composable
fun CardItem(title: String, cash: Float, onMoreClick: () -> Unit = {}) {
    val textColor = when (title) {
        "Owe Them" -> DarkRed
        "Owe You" -> DarkGreen
        "Expenses" -> Color.Red
        "Income" -> Color.Green
        "Total" -> if (cash >= 0) Color.Green else Color.Red
        else -> MaterialTheme.colorScheme.onBackground
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.inverseOnSurface
        ),
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
            IconButton(onClick = onMoreClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun CardSection() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val walletDao = db.walletDao()
    val paidItemDao = db.paidItemDao()
    val coroutineScope = rememberCoroutineScope()

    var budget by remember { mutableStateOf(Budget(0f, 0f, 0f, 0f, 0f)) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            val paidItems = paidItemDao.getAllPaidItems().first()
            val myselfWallet = walletDao.getWalletByOwner("Myself").first()
            val otherWallets = walletDao.getAllWallets().first().filter { it.owner != "Myself" }

            val income = paidItems.filter { it.walletId == myselfWallet.id && it.isPaid }.sumOf { it.price }.toFloat()
            val expenses = paidItems.filter { it.walletId == myselfWallet.id && !it.isPaid }.sumOf { it.price }.toFloat()

            val oweYouOtherWallets = otherWallets.sumOf { wallet ->
                paidItems.filter { it.walletId == wallet.id && it.isPaid }.sumOf { it.price }
            }.toFloat()

            val oweThemOtherWallets = otherWallets.sumOf { wallet ->
                paidItems.filter { it.walletId == wallet.id && !it.isPaid }.sumOf { it.price }
            }.toFloat()

            val total = income + oweYouOtherWallets - (expenses + oweThemOtherWallets)

            budget = Budget(income, expenses, oweYouOtherWallets, oweThemOtherWallets, total)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        item {
            CardItem("Income", budget.income, onMoreClick = { showDialog = true })
        }
        item { CardItem("Expenses", budget.expenses) }
        item { CardItem("Owe You", budget.oweYou) }
        item { CardItem("Owe Them", budget.oweThem) }
        item { CardItem("Total", budget.total) }
    }

    if (showDialog) {
        TopUpDialog(
            onDismiss = { showDialog = false },
            onTopUp = { amount ->
                coroutineScope.launch(Dispatchers.IO) {
                    val myselfWallet = walletDao.getWalletByOwner("Myself").first()
                    val newIncome = budget.income + amount
                    budget = budget.copy(income = newIncome, total = newIncome + budget.oweYou - (budget.expenses + budget.oweThem))

                    // Optionally update the database if needed
                    val updatedWallet = myselfWallet.copy(amountPaid = myselfWallet.amountPaid + amount)
                    walletDao.update(updatedWallet)
                }
            }
        )
    }
}

@Preview
@Composable
fun PreviewCardSection() {
    CardSection()
}
