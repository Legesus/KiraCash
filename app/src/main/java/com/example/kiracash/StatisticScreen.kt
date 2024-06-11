package com.example.kiracash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kiracash.model.AppDatabase
import com.example.kiracash.model.Wallet
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    val context = LocalContext.current
    val walletDao = AppDatabase.getDatabase(context).walletDao()
    var wallets by remember { mutableStateOf(emptyList<Wallet>()) }
    var totalAmountPaid by remember { mutableStateOf(0.0) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            walletDao.getWalletsWithTotalAmountPaid().collect { walletList ->
                wallets = walletList
                totalAmountPaid = walletList.sumOf { it.amountPaid }
            }
        }
    }

    val slices = wallets.map { wallet ->
        PieChartData.Slice(
            value = (wallet.amountPaid / totalAmountPaid).toFloat(),
            color = Color((0xFF000000..0xFFFFFFFF).random()) // Random color for each slice
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
                    Text(
                        text = "${wallet.owner}: RM${wallet.amountPaid}",
                        color = Color.White,
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
