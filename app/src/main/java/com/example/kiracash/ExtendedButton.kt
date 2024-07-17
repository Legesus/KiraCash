package com.example.kiracash

import android.Manifest
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.kiracash.model.AppDatabase
import com.example.kiracash.model.GoalSet
import com.example.kiracash.model.PaidItem
import com.example.kiracash.model.Receipt
import com.example.kiracash.model.ReceiptDao
import com.example.kiracash.model.ReceiptItemJoin
import com.example.kiracash.model.Wallet
import com.example.kiracash.model.WalletItemJoin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExtendedButton(
    sharedViewModel: SharedViewModel,
    imageProcessor: ImageProcessor,
    navController: NavHostController,
    onSaveToGoalsClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    val showLoading = remember { mutableStateOf(false) }

    val walletDao = AppDatabase.getDatabase(context).walletDao()
    val receiptDao = AppDatabase.getDatabase(context).receiptDao()
    val goalSetDao = AppDatabase.getDatabase(context).goalSetDao()

    var walletsState by remember { mutableStateOf<List<Wallet>>(emptyList()) }
    var goalsList by remember { mutableStateOf(listOf<GoalSet>()) }

    LaunchedEffect(Unit) {
        scope.launch {
            walletDao.getAllWallets().collect { wallets ->
                walletsState = wallets
            }
            goalSetDao.getAllGoals().collect { goals ->
                goalsList = goals
                Log.d("ExtendedButton", "Goals retrieved: $goalsList")
            }
        }
    }

    // Camera Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { imageBitmap ->
            if (imageBitmap != null) {
                handleImageProcessing(
                    scope,
                    showLoading,
                    imageProcessor,
                    imageBitmap,
                    sharedViewModel,
                    receiptDao,
                    walletsState
                )
            }
        }
    )

    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                handleImageProcessing(
                    scope,
                    showLoading,
                    imageProcessor,
                    bitmap,
                    sharedViewModel,
                    receiptDao,
                    walletsState
                )
            }
        }
    )

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launcher.launch(null)
        } else {
            // Handle permission denial
        }
    }

    val hasCameraPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 72.dp, end = 16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(animationSpec = tween(200)),
            exit = shrinkVertically(animationSpec = tween(200))
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        sharedViewModel.showGoalDialog.value = true
                        sharedViewModel.selectedGoal.value = null
                        sharedViewModel.amountToSave.value = ""
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Add expense towards Goals", color = Color.White)
                }

                Button(
                    onClick = {
                        galleryLauncher.launch("image/*")
                        navController.navigate(MainDestinations.QR_MENU_ROUTE)
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Upload Receipt", color = Color.White)
                }

                Button(
                    onClick = {
                        if (hasCameraPermission) {
                            launcher.launch(null)
                            navController.navigate(MainDestinations.QR_MENU_ROUTE)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Scan Receipt", color = Color.White)
                }

                Button(
                    onClick = onSaveToGoalsClick,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Add personal expenses", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = Color(0xFF1DB954),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(Icons.Rounded.QrCode, contentDescription = "Add", tint = Color.White)
        }
    }

    // Show loading dialog if showLoading is true
    if (showLoading.value) {
        LoadingDialog()
    }

    // Show ReceiptDialog if items are extracted
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

    // GoalDialog
    if (sharedViewModel.showGoalDialog.value) {
        GoalDialog(
            goalsList = goalsList,
            selectedGoal = sharedViewModel.selectedGoal,
            amountToSave = sharedViewModel.amountToSave,
            onSave = {
                val selectedGoal = sharedViewModel.selectedGoal.value
                val amountToSave = sharedViewModel.amountToSave.value.toDoubleOrNull()

                if (selectedGoal != null && amountToSave != null) {
                    scope.launch(Dispatchers.IO) {
                        val updatedGoal = selectedGoal.copy(amountSaved = selectedGoal.amountSaved + amountToSave)
                        goalSetDao.updateGoal(updatedGoal)
                    }
                    sharedViewModel.showGoalDialog.value = false
                    sharedViewModel.selectedGoal.value = null
                    sharedViewModel.amountToSave.value = ""
                }
            },
            onDismiss = {
                sharedViewModel.showGoalDialog.value = false
                sharedViewModel.selectedGoal.value = null
                sharedViewModel.amountToSave.value = ""
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDialog(
    goalsList: List<GoalSet>,
    selectedGoal: MutableState<GoalSet?>,
    amountToSave: MutableState<String>,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense to Goal") },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedGoal.value?.title ?: "Select Goal",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Goal") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        goalsList.forEach { goal ->
                            DropdownMenuItem(
                                text = { Text(goal.title) },
                                onClick = {
                                    selectedGoal.value = goal
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountToSave.value,
                    onValueChange = { amountToSave.value = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onSave) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun handleImageProcessing(
    scope: CoroutineScope,
    showLoading: MutableState<Boolean>,
    imageProcessor: ImageProcessor,
    imageBitmap: android.graphics.Bitmap,
    sharedViewModel: SharedViewModel,
    receiptDao: ReceiptDao,
    walletsState: List<Wallet>
) {
    scope.launch(Dispatchers.IO) {
        showLoading.value = true
        val processedItems = imageProcessor.processImage(imageBitmap)
        withContext(Dispatchers.Main) {
            sharedViewModel.extractedItems.value = processedItems
            sharedViewModel.showReceiptDialog.value = true
            showLoading.value = false
        }
    }
}