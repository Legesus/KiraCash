@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kiracash.model.AppDatabase
import com.example.kiracash.model.PaidItem
import com.example.kiracash.model.Wallet
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DebtMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            DebtMenuScreen(navController)
        }
    }
}

@Composable
fun DebtMenuScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val walletDao = db.walletDao()
    val coroutineScope = rememberCoroutineScope()

    var wallets by remember { mutableStateOf(emptyList<Wallet>()) }
    var selectedWallet by remember { mutableStateOf<Wallet?>(null) }
    var showAmountOwe by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        selectedWallet = wallets.firstOrNull()
        coroutineScope.launch {
            walletDao.getAllWallets().collect { walletList ->
                Log.d("DebtMenuScreen", "Loaded wallets: $walletList")
                wallets = walletList
                if (walletList.isNotEmpty()) {
                    selectedWallet = walletList[0]
                    Log.d("DebtMenuScreen", "Selected wallet: $selectedWallet")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Wallet Menu",
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFF1C1B22))
                .padding(16.dp)
        ) {
            selectedWallet?.let { wallet ->
                WalletOverview(walletDao.getWalletById(wallet.id), coroutineScope)
            }
            WalletDropdown(
                wallets = wallets,
                selectedWallet = selectedWallet,
                onWalletSelected = { selectedWallet = it },
                showAmountOwe = showAmountOwe,
                onShowAmountOweChange = { showAmountOwe = it },
                context = context // Pass context down to WalletDropdown
            )
        }
    }
}


@Composable
fun WalletDropdown(
    wallets: List<Wallet>,
    selectedWallet: Wallet?,
    onWalletSelected: (Wallet) -> Unit,
    showAmountOwe: Boolean,
    onShowAmountOweChange: (Boolean) -> Unit,
    context: android.content.Context // Receive context
) {
    val coroutineScope = rememberCoroutineScope()
    val paidItemDao = AppDatabase.getDatabase(context).paidItemDao()
    var expanded by remember { mutableStateOf(false) }
    var items by remember { mutableStateOf(emptyList<PaidItem>()) }

    LaunchedEffect(selectedWallet) {
        selectedWallet?.let {
            coroutineScope.launch {
                Log.d("WalletDropdown", "Loading items for wallet: ${selectedWallet.id}")
                items = paidItemDao.getAllPaidItems().first().filter { it.walletId == selectedWallet.id }
                Log.d("WalletDropdown", "Loaded items: $items")
            }
        }
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Select Wallet",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Toggle Switch
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Paid", color = Color.White)
                Switch(
                    checked = showAmountOwe,
                    onCheckedChange = onShowAmountOweChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Green)
                )
                Text("Owe", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                readOnly = true,
                value = selectedWallet?.owner ?: "",
                onValueChange = {},
                label = { Text("Wallet") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                wallets.forEach { wallet ->
                    DropdownMenuItem(
                        text = { Text(wallet.owner) },
                        onClick = {
                            Log.d("WalletDropdown", "Wallet selected: $wallet")
                            onWalletSelected(wallet)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ItemsList(items = items, showAmountOwe = showAmountOwe)
    }
}

@Composable
fun ItemsList(items: List<PaidItem>, showAmountOwe: Boolean) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(items.filter { it.isPaid != showAmountOwe }) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.name} - RM ${item.price}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Checkbox(
                    checked = !showAmountOwe,
                    onCheckedChange = {},
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF509BFF))
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DebtMenuPreview() {
    val mockNavController = rememberNavController()
    DebtMenuScreen(navController = mockNavController)
}
