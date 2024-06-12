package com.example.kiracash

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kiracash.model.AppDatabase
import com.example.kiracash.model.Item
import com.example.kiracash.model.Receipt
import com.example.kiracash.model.ReceiptItemJoin
import com.example.kiracash.model.Wallet
import com.example.kiracash.model.WalletItemJoin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
fun ReceiptDialog(receipt: Receipt, items: List<Item>, wallets: List<Wallet>, onDismiss: () -> Unit, onFinalize: (Map<Item, Wallet>) -> Unit) {
    // Create a mutable state for each item to hold the selected wallet
    val selectedWallets = remember { mutableStateMapOf<Item, Wallet>() }

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
                            text = String.format("%-20s RM %s", item.name, item.price),
                            modifier = Modifier.weight(1f)
                        )

                        var expanded by remember { mutableStateOf(false) }
                        var selectedWallet by remember { mutableStateOf(if (wallets.isNotEmpty()) wallets[0] else null) }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            TextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                readOnly = true,
                                value = selectedWallet?.owner ?: "",
                                onValueChange = {},
                                label = { Text("Select Wallet") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                wallets.forEach { wallet ->
                                    DropdownMenuItem(
                                        text = { Text(wallet.owner) },
                                        onClick = {
                                            selectedWallet = wallet
                                            expanded = false

                                            // Update the selected wallet for this item
                                            selectedWallets[item] = wallet
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
                        Log.d("ReceiptDialog", "Size of selectedWallets: ${selectedWallets.size}")
                        onFinalize(selectedWallets)
                        onDismiss()  // Dismiss the dialog after finalizing
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
            text = "Receipt History",
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
    val jsonString = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val showLoading = remember { mutableStateOf(false) }

    val selectedReceipt = remember { mutableStateOf<Receipt?>(null) }
    val receiptItems = remember { mutableStateOf<List<Item>>(emptyList()) }

    // Create an instance of WalletDao
    val walletDao = AppDatabase.getDatabase(context).walletDao()
    val receiptDao = AppDatabase.getDatabase(context).receiptDao()

    // Create a mutable state to hold the list of wallets
    var walletsState by remember { mutableStateOf<List<Wallet>>(emptyList()) }

    // Create a coroutine scope
    val scope = rememberCoroutineScope()

    // Observe the wallets from the database
    LaunchedEffect(Unit) {
        scope.launch {
            walletDao.getAllWallets().collect { wallets ->
                walletsState = wallets
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { imageBitmap ->
            if (imageBitmap != null) {
                scope.launch(Dispatchers.IO) {
                    showLoading.value = true  // Show loading dialog
                    val processedItems = imageProcessor.processImage(imageBitmap)
                    jsonString.value = imageProcessor.getJsonString()
                    showDialog.value = true
                    showLoading.value = false  // Hide loading dialog
                }
            }
        }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                scope.launch(Dispatchers.IO) {
                    showLoading.value = true  // Show loading dialog
                    val processedItems = imageProcessor.processImage(bitmap)
                    jsonString.value = imageProcessor.getJsonString()
                    showDialog.value = true
                    showLoading.value = false  // Hide loading dialog
                }
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launcher.launch(null)
        } else {
            // Handle permission denial (not implemented here)
        }
    }

    val hasCameraPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Receipt Menu",
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

                // Include the receipt history below the TopAppBar
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
                    LoadingDialog()  // Display the loading dialog
                }

                if (showDialog.value) {
                    val items = imageProcessor.itemsState.value
                    val wallets = walletsState

                    ReceiptDialog(
                        receipt = Receipt(),  // Replace with the actual Receipt
                        items = items,
                        wallets = wallets,
                        onDismiss = {
                            imageProcessor.itemsState.value = emptyList()
                            showDialog.value = false
                        },
                        onFinalize = { selectedWallets ->
                            scope.launch(Dispatchers.IO) {
                                val walletItemJoinDao = AppDatabase.getDatabase(context).walletItemJoinDao()
                                val receiptItemJoinDao = AppDatabase.getDatabase(context).receiptItemJoinDao()

                                val receipt = Receipt() // Replace with the actual code to create a Receipt
                                val receiptDao = AppDatabase.getDatabase(context).receiptDao()
                                val receiptId = receiptDao.insert(receipt)

                                selectedWallets.forEach { (item, wallet) ->
                                    val walletJoin = WalletItemJoin(walletId = wallet.id, itemId = item.id)
                                    walletItemJoinDao.insert(walletJoin)

                                    val receiptJoin = ReceiptItemJoin(receiptId = receiptId.toInt(), itemId = item.id)
                                    receiptItemJoinDao.insert(receiptJoin)
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (hasCameraPermission) {
                                launcher.launch(null)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text("Scan", color = Color.White)
                    }

                    Button(
                        onClick = {
                            galleryLauncher.launch("image/*")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text("Upload", color = Color.White)
                    }
                }
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
            color = Color.Black.copy(alpha = 0.1f) // Semi-transparent background
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
