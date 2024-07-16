package com.example.kiracash

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kiracash.model.AppDatabase
import com.example.kiracash.model.MonthlyCategoryExpense
import com.example.kiracash.model.Wallet
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import kotlinx.coroutines.flow.first

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

    val personalItemDao = AppDatabase.getDatabase(context).personalItemDao()
    Log.d("StatisticScreen", "Got personalItemDao: $personalItemDao")

    val paidItemsFlow = paidItemDao.getAllPaidItems().collectAsState(initial = emptyList())

    var wallets by remember { mutableStateOf(emptyList<Wallet>()) }
    Log.d("StatisticScreen", "Initialized wallets state")

    var totalAmount by remember { mutableStateOf(0.0) }
    Log.d("StatisticScreen", "Initialized totalAmount state")

    var showAmountOwe by remember { mutableStateOf(false) }
    Log.d("StatisticScreen", "Initialized showAmountOwe state")

    var personalExpenses by remember { mutableStateOf(emptyList<MonthlyCategoryExpense>()) }
    Log.d("StatisticScreen", "Initialized personalExpenses state")

    LaunchedEffect(showAmountOwe, paidItemsFlow.value) {
        Log.d("StatisticScreen", "LaunchedEffect started")

        try {
            // Fetch personal expenses
            Log.d("StatisticScreen", "Fetching personal expenses")
            personalExpenses = personalItemDao.getMonthlyCategoryExpenses().first()
            Log.d("StatisticScreen", "Fetched Personal Expenses: $personalExpenses")
        } catch (e: Exception) {
            Log.e("StatisticScreen", "Error fetching personal expenses", e)
        }

        if (showAmountOwe) {
            walletDao.getWalletsWithTotalAmountOwe().collect { walletList ->
                wallets = walletList
                totalAmount = walletList.sumOf { wallet -> wallet.amountOwe }
                Log.d("StatisticScreen", "Total Amount Owe: $totalAmount")
                walletList.forEach { wallet ->
                    Log.d("StatisticScreen", "Wallet: ${wallet.owner}, Amount Owe: ${wallet.amountOwe}")
                }
            }
        } else {
            walletDao.getWalletsWithTotalAmountPaid().collect { walletList ->
                wallets = walletList.map { wallet ->
                    val paidItems = paidItemsFlow.value.filter { paidItem -> paidItem.walletId == wallet.id && paidItem.isPaid }
                    wallet.copy(amountPaid = paidItems.sumOf { paidItem -> paidItem.price })
                }
                totalAmount = wallets.sumOf { wallet -> wallet.amountPaid }
                Log.d("StatisticScreen", "Total Amount Paid: $totalAmount")
                wallets.forEach { wallet ->
                    Log.d("StatisticScreen", "Wallet: ${wallet.owner}, Amount Paid: ${wallet.amountPaid}")
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
                .background(Color(0xFF1C1B24))
                .verticalScroll(rememberScrollState()),
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

            // Personal Expenses Section with Bar Chart
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Personal Expenses",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))


            MonthlyCategoryBarChartView(personalExpenses)
        }
    }
}

@Composable
fun WalletBarChartView(wallets: List<Wallet>, showAmountOwe: Boolean) {
    AndroidView(factory = { context ->
        BarChart(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            description.isEnabled = false
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawGridBackground(false)

            val walletNames = wallets.map { it.owner }
            val amounts = wallets.map { if (showAmountOwe) it.amountOwe.toFloat() else it.amountPaid.toFloat() }

            val entries = amounts.mapIndexed { index, amount ->
                BarEntry(index.toFloat(), amount)
            }

            val dataSet = BarDataSet(entries, "Wallets").apply {
                colors = List(wallets.size) { index -> wallets[index].walletColor.toInt() }
                valueTextColor = android.graphics.Color.WHITE
                valueTextSize = 12f
            }

            val barData = BarData(dataSet)
            data = barData

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                valueFormatter = IndexAxisValueFormatter(walletNames)
                granularity = 1f
                textColor = android.graphics.Color.WHITE
                textSize = 12f
            }

            axisLeft.apply {
                setDrawGridLines(false)
                textColor = android.graphics.Color.WHITE
                textSize = 12f
            }

            axisRight.isEnabled = false

            legend.apply {
                isEnabled = true
                textColor = android.graphics.Color.WHITE
                textSize = 12f
            }

            invalidate()
        }
    }, update = { view ->
        (view as BarChart).apply {
            val walletNames = wallets.map { it.owner }
            val amounts = wallets.map { if (showAmountOwe) it.amountOwe.toFloat() else it.amountPaid.toFloat() }

            val entries = amounts.mapIndexed { index, amount ->
                BarEntry(index.toFloat(), amount)
            }

            val dataSet = BarDataSet(entries, "Wallets").apply {
                colors = List(wallets.size) { index -> wallets[index].walletColor.toInt() }
                valueTextColor = android.graphics.Color.WHITE
                valueTextSize = 12f
            }

            val barData = BarData(dataSet)
            data = barData

            xAxis.valueFormatter = IndexAxisValueFormatter(walletNames)
            invalidate()
        }
    },
    modifier = Modifier
        .fillMaxWidth()
        .height(400.dp) // Adjust height as needed
    )
}

@Composable
fun MonthlyCategoryBarChartView(monthlyCategoryExpenses: List<MonthlyCategoryExpense>) {
    AndroidView(factory = { context ->
        BarChart(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            description.isEnabled = false
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawGridBackground(false)

            val months = monthlyCategoryExpenses.map { it.month }.distinct()
            val categories = monthlyCategoryExpenses.map { it.category }.distinct()

            val categoryColors = mapOf(
                "Food & Drink" to android.graphics.Color.RED,
                "Entertainment" to android.graphics.Color.BLUE,
                "Health & Fitness" to android.graphics.Color.YELLOW
            )

            val entries = categories.map { category ->
                BarDataSet(
                    months.mapIndexed { index, month ->
                        val total = monthlyCategoryExpenses
                            .filter { it.month == month && it.category == category }
                            .sumOf { it.total }
                        BarEntry(index.toFloat(), total.toFloat())
                    }, category
                ).apply {
                    color = categoryColors[category] ?: android.graphics.Color.GRAY
                    valueTextColor = android.graphics.Color.WHITE
                    valueTextSize = 12f
                }
            }

            val barData = BarData(entries)
            data = barData

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                valueFormatter = IndexAxisValueFormatter(months)
                granularity = 1f
                textColor = android.graphics.Color.WHITE
                textSize = 12f
            }

            axisLeft.apply {
                setDrawGridLines(false)
                textColor = android.graphics.Color.WHITE
                textSize = 12f
            }

            axisRight.isEnabled = false

            legend.apply {
                isEnabled = true
                textColor = android.graphics.Color.WHITE
                textSize = 12f
            }

            invalidate()
        }
    }, update = { view ->
        (view as BarChart).apply {
            val months = monthlyCategoryExpenses.map { it.month }.distinct()
            val categories = monthlyCategoryExpenses.map { it.category }.distinct()

            val categoryColors = mapOf(
                "Food & Drink" to android.graphics.Color.RED,
                "Entertainment" to android.graphics.Color.BLUE,
                "Health & Fitness" to android.graphics.Color.YELLOW
            )

            val entries = categories.map { category ->
                BarDataSet(
                    months.mapIndexed { index, month ->
                        val total = monthlyCategoryExpenses
                            .filter { it.month == month && it.category == category }
                            .sumOf { it.total }
                        BarEntry(index.toFloat(), total.toFloat())
                    }, category
                ).apply {
                    color = categoryColors[category] ?: android.graphics.Color.GRAY
                    valueTextColor = android.graphics.Color.WHITE
                    valueTextSize = 12f
                }
            }

            val barData = BarData(entries)
            data = barData

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(months)
                granularity = 1f
                textColor = android.graphics.Color.WHITE
                textSize = 12f
            }

            invalidate()
        }
    },
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp) // Increased height
    )
}


@Preview(showBackground = true)
@Composable
fun StatisticScreenPreview() {
    val mockNavController = rememberNavController()
    StatisticScreen(navController = mockNavController)
}

