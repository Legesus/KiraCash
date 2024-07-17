package com.example.kiracash

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kiracash.model.AppDatabase
import com.example.kiracash.model.Item
import com.example.kiracash.model.PaidItem
import com.example.kiracash.model.Receipt
import com.example.kiracash.model.ReceiptItemJoin
import com.example.kiracash.model.Wallet
import com.example.kiracash.model.WalletItemJoin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OCRActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OCRScreen(navController = rememberNavController())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptDialog(
    receipt: Receipt,
    items: List<Item>,
    wallets: List<Wallet>,
    onDismiss: () -> Unit,
    onFinalize: (Map<Item, Pair<Wallet?, Boolean>>) -> Unit
) {
    val selectedWalletsAndStatus = remember { mutableStateMapOf<Item, Pair<Wallet?, Boolean>>() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Receipt ID: ${receipt.id}",
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 14.sp),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                items.forEach { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${item.name}\nRM ${item.price}",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            var expandedWallet by remember { mutableStateOf(false) }
                            var selectedWallet by remember { mutableStateOf<Wallet?>(if (wallets.isNotEmpty()) wallets[0] else null) }
                            var expandedStatus by remember { mutableStateOf(false) }
                            var selectedStatus by remember { mutableStateOf(true) }

                            // Wallet Dropdown
                            ExposedDropdownMenuBox(
                                expanded = expandedWallet,
                                onExpandedChange = { expandedWallet = !expandedWallet },
                                modifier = Modifier.weight(1f)
                            ) {
                                TextField(
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    readOnly = true,
                                    value = selectedWallet?.owner ?: "Select Wallet",
                                    onValueChange = {},
                                    label = { Text("Wallet", fontSize = 10.sp) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedWallet) },
                                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedWallet,
                                    onDismissRequest = { expandedWallet = false },
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("None", fontSize = 12.sp) },
                                        onClick = {
                                            selectedWallet = null
                                            expandedWallet = false

                                            selectedWalletsAndStatus[item] = Pair(null, selectedStatus)
                                        },
                                    )
                                    wallets.forEach { wallet ->
                                        DropdownMenuItem(
                                            text = { Text(wallet.owner, fontSize = 12.sp) },
                                            onClick = {
                                                selectedWallet = wallet
                                                expandedWallet = false

                                                selectedWalletsAndStatus[item] = Pair(wallet, selectedStatus)
                                            },
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Status Dropdown
                            ExposedDropdownMenuBox(
                                expanded = expandedStatus,
                                onExpandedChange = { expandedStatus = !expandedStatus },
                                modifier = Modifier.weight(1f)
                            ) {
                                TextField(
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    readOnly = true,
                                    value = if (selectedStatus) "Owe You (+)" else "Owe Them (-)",
                                    onValueChange = {},
                                    label = { Text("Status", fontSize = 10.sp) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedStatus,
                                    onDismissRequest = { expandedStatus = false },
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Owe You", fontSize = 12.sp) },
                                        onClick = {
                                            selectedStatus = true
                                            expandedStatus = false

                                            selectedWalletsAndStatus[item] = Pair(selectedWallet, selectedStatus)
                                        },
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Owe Them", fontSize = 12.sp) },
                                        onClick = {
                                            selectedStatus = false
                                            expandedStatus = false

                                            selectedWalletsAndStatus[item] = Pair(selectedWallet, selectedStatus)
                                        },
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        Log.d("ReceiptDialog", "Size of selectedWalletsAndStatus: ${selectedWalletsAndStatus.size}")
                        onFinalize(selectedWalletsAndStatus)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Finalize", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ReceiptItemsDialog(
    receipt: Receipt,
    items: List<Item>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Receipt ID: ${receipt.id}",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${item.name} - RM ${item.price}",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ReceiptHistory(
    onReceiptClick: (Receipt) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val receiptDao = db.receiptDao()

    val receipts by receiptDao.getAllReceipts().collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0xFF1C1B24))
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Transaction History",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        receipts.forEach { receipt ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onReceiptClick(receipt) },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF27273F))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Receipt ID: ${receipt.id}",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OCRScreen(navController: NavHostController) {
    val context = LocalContext.current
    val imageProcessor = remember { ImageProcessor(context) }
    val sharedViewModel: SharedViewModel = viewModel()
    val jsonString = remember { mutableStateOf("") }
    val showLoading = remember { mutableStateOf(false) }

    val selectedReceipt = remember { mutableStateOf<Receipt?>(null) }
    val receiptItems = remember { mutableStateOf<List<Item>>(emptyList()) }

    val walletDao = AppDatabase.getDatabase(context).walletDao()
    val receiptDao = AppDatabase.getDatabase(context).receiptDao()

    var walletsState by remember { mutableStateOf<List<Wallet>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            walletDao.getAllWallets().collect { wallets ->
                walletsState = wallets
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transaction Menu",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                ReceiptHistory(
                    onReceiptClick = { receipt ->
                        scope.launch(Dispatchers.IO) {
                            val items = receiptDao.getItemsForReceipt(receipt.id)
                            receiptItems.value = items
                            selectedReceipt.value = receipt
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                if (showLoading.value) {
                    LoadingDialog()
                }

                if (sharedViewModel.showReceiptDialog.value) {
                    val items = imageProcessor.itemsState.value
                    val wallets = walletsState

                    ReceiptDialog(
                        receipt = Receipt(),
                        items = items,
                        wallets = wallets,
                        onDismiss = {
                            sharedViewModel.extractedItems.value = emptyList()
                            sharedViewModel.showReceiptDialog.value = false
                        },
                        onFinalize = { selectedWalletsAndPaidStatus ->
                            scope.launch(Dispatchers.IO) {
                                val walletItemJoinDao = AppDatabase.getDatabase(context).walletItemJoinDao()
                                val receiptItemJoinDao = AppDatabase.getDatabase(context).receiptItemJoinDao()
                                val paidItemDao = AppDatabase.getDatabase(context).paidItemDao()

                                val receipt = Receipt()
                                val receiptId = receiptDao.insert(receipt)

                                selectedWalletsAndPaidStatus.forEach { (item, pair) ->
                                    val (wallet, isPaid) = pair

                                    wallet?.let {
                                        val walletJoin = WalletItemJoin(walletId = it.id, itemId = item.id)
                                        walletItemJoinDao.insert(walletJoin)
                                    }

                                    val receiptJoin = ReceiptItemJoin(receiptId = receiptId.toInt(), itemId = item.id)
                                    receiptItemJoinDao.insert(receiptJoin)

                                    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                                    val paidItem = PaidItem(name = item.name, price = item.price, isPaid = isPaid, walletId = wallet?.id ?: 0, datePaid = currentDate)
                                    paidItemDao.insert(paidItem)
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = jsonString.value,
                color = Color.White,
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
            )

            selectedReceipt.value?.let { receipt ->
                ReceiptItemsDialog(
                    receipt = receipt,
                    items = receiptItems.value,
                    onDismiss = { selectedReceipt.value = null }
                )
            }
        }
    }
}

@Composable
fun LoadingDialog() {
    Dialog(onDismissRequest = {}) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOCRScreen() {
    val mockNavController = rememberNavController()
    OCRScreen(navController = mockNavController)
}
