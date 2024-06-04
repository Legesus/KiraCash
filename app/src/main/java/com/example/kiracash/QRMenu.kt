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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kiracash.model.AppDatabase
import com.example.kiracash.model.Item
import com.example.kiracash.model.Receipt
import com.example.kiracash.model.Wallet
import kotlinx.coroutines.CoroutineScope
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
fun ReceiptDialog(receipt: Receipt, items: List<Item>, wallets: List<Wallet>, onDismiss: () -> Unit, onFinalize: (List<Item>) -> Unit) {
    var selectedWalletIndex by remember { mutableStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Column {
            Text(text = "Receipt ID: ${receipt.id}")

            items.forEach { item ->
                Row {
                    Text(text = "${item.name}: ${item.price}")

                    var expanded by remember { mutableStateOf(false) }
                    var selectedWallet by remember { mutableStateOf(wallets[selectedWalletIndex].owner) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                    ) {
                        TextField(
                            modifier = Modifier.menuAnchor(),
                            readOnly = true,
                            value = selectedWallet,
                            onValueChange = {},
                            label = { Text("Select Wallet") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            wallets.forEachIndexed { index, wallet ->
                                DropdownMenuItem(
                                    text = { Text(wallet.owner) },
                                    onClick = {
                                        selectedWallet = wallet.owner
                                        selectedWalletIndex = index
                                        expanded = false
                                    },
                                )
                            }
                        }
                    }
                }
            }

            // Add a "Finalize" button at the end of the dialog
            Button(
                onClick = { onFinalize(items) },  // Call the onFinalize function when the button is clicked
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
            ) {
                Text("Finalize", color = Color.White)
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

    // Add this line to create an instance of itemDao
    val itemDao = AppDatabase.getDatabase(context).itemDao()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { imageBitmap ->
            if (imageBitmap != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    imageProcessor.processImage(imageBitmap)
                    jsonString.value = imageProcessor.getJsonString()

                    // Add this line to show the dialog after processing the image
                    showDialog.value = true
                }
            }
        }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                CoroutineScope(Dispatchers.IO).launch {
                    imageProcessor.processImage(bitmap)

                    // Add this line to show the dialog after processing the image
                    showDialog.value = true
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
            CenterAlignedTopAppBar(
                title = { Text("Receipt Scanner") },
                navigationIcon = {
                    IconButton(onClick = { Log.d("OCRScreen", "Menu button clicked") }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1F1B24)
                )
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Add this block to show the dialog when showDialog is true
            if (showDialog.value) {
                // Fetch the items and wallets from the database
                // This is a placeholder and you'll need to replace it with your actual code
                val items = listOf<Item>()
                val wallets = listOf<Wallet>()

                // Log the items and wallets
                Log.d("OCRScreen", "Items: $items")
                Log.d("OCRScreen", "Wallets: $wallets")

                ReceiptDialog(
                    receipt = Receipt(),  // Replace with the actual Receipt
                    items = items,
                    wallets = wallets,
                    onDismiss = { showDialog.value = false },
                    onFinalize = { finalizedItems ->
                        CoroutineScope(Dispatchers.IO).launch {
                            itemDao.insertAll(finalizedItems)
                        }
                    }
                )
            }

            Button(
                onClick = {
                    if (hasCameraPermission) {
                        launcher.launch(null)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
            ) {
                Text("Scan Receipt", color = Color.White)
            }

            Button(
                onClick = {
                    galleryLauncher.launch("image/*")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
            ) {
                Text("Upload Picture from Gallery", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(jsonString.value, color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOCRScreen() {
    val mockNavController = rememberNavController()
    OCRScreen(navController = mockNavController)
}