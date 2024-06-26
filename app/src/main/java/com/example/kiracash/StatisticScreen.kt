package com.example.kiracash

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kiracash.model.AppDatabase
import com.example.kiracash.model.Wallet
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer

class StatisticScreen : ComponentActivity() {
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
    Log.d("StatisticScreen", "Start of StatisticScreen function")

    val context = LocalContext.current
    Log.d("StatisticScreen", "Got LocalContext: $context")

    val walletDao = AppDatabase.getDatabase(context).walletDao()
    Log.d("StatisticScreen", "Got walletDao: $walletDao")

    val paidItemDao = AppDatabase.getDatabase(context).paidItemDao()
    Log.d("StatisticScreen", "Got paidItemDao: $paidItemDao")

    // Collect paid items
    val paidItemsFlow = paidItemDao.getAllPaidItems().collectAsState(initial = emptyList())

    var wallets by remember { mutableStateOf(emptyList<Wallet>()) }
    Log.d("StatisticScreen", "Initialized wallets state")

    var totalAmount by remember { mutableStateOf(0.0) }
    Log.d("StatisticScreen", "Initialized totalAmount state")

    var showAmountOwe by remember { mutableStateOf(false) }
    Log.d("StatisticScreen", "Initialized showAmountOwe state")

    LaunchedEffect(showAmountOwe, paidItemsFlow.value) {
        if (showAmountOwe) {
            walletDao.getWalletsWithTotalAmountOwe().collect { walletList ->
                wallets = walletList
                totalAmount = walletList.sumOf { it.amountOwe }
                Log.d("StatisticScreen", "Total Amount Owe: $totalAmount")
                walletList.forEach {
                    Log.d("StatisticScreen", "Wallet: ${it.owner}, Amount Owe: ${it.amountOwe}")
                }
            }
        } else {
            walletDao.getWalletsWithTotalAmountPaid().collect { walletList ->
                // Map wallets to include the sum of paid items
                wallets = walletList.map { wallet ->
                    val paidItems = paidItemsFlow.value.filter { it.walletId == wallet.id && it.isPaid }
                    wallet.copy(amountPaid = paidItems.sumOf { it.price })
                }
                totalAmount = wallets.sumOf { it.amountPaid }
                Log.d("StatisticScreen", "Total Amount Paid: $totalAmount")
                wallets.forEach {
                    Log.d("StatisticScreen", "Wallet: ${it.owner}, Amount Paid: ${it.amountPaid}")
                }
            }
        }
    }

    val slices = wallets.map { wallet ->
        val colorHex = "#" + Integer.toHexString(wallet.walletColor).padStart(6, '0')
        PieChartData.Slice(
            value = if (showAmountOwe) (wallet.amountOwe / totalAmount).toFloat() else (wallet.amountPaid / totalAmount).toFloat(),
            color = Color(android.graphics.Color.parseColor(colorHex))
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Statistic Menu",
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF1C1B24)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Toggle Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("They Owe You ")
                Switch(
                    checked = showAmountOwe,
                    onCheckedChange = { showAmountOwe = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Green)
                )
                Text(" You owe them")
            }

            // Title and Chart
            Text(
                text = "Statistic",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Pie chart implementation
            val sliceThickness = 100f

            PieChart(
                pieChartData = PieChartData(slices),
                modifier = Modifier.size(200.dp),
                animation = simpleChartAnimation(),
                sliceDrawer = SimpleSliceDrawer(sliceThickness)
            )

            // Item List
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                wallets.forEach { wallet ->
                    val colorHex = "#" + Integer.toHexString(wallet.walletColor).padStart(6, '0')
                    Text(
                        text = "${wallet.owner}: RM${if (showAmountOwe) wallet.amountOwe else wallet.amountPaid}",
                        color = Color(android.graphics.Color.parseColor(colorHex)),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
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
