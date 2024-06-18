import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.kiracash.model.AppDatabase
import com.example.kiracash.model.Wallet
import kotlinx.coroutines.launch
import coil.compose.rememberImagePainter
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.ARGBPickerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWalletUI(navController: NavController) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val walletDao = db.walletDao()
    val wallets = walletDao.getAllWallets().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    // State for dialog visibility, wallet name, wallet picture, and wallet color
    val showDialog = remember { mutableStateOf(false) }
    val walletName = remember { mutableStateOf("") }
    val walletPicture = remember { mutableStateOf("") }
    val walletColor = remember { mutableStateOf(Color.White) }
    val tempWalletColor = remember { mutableStateOf(Color.White) } // Temporary color state

    // Color picker dialog state
    val colorPickerDialogState = rememberMaterialDialogState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Wallet") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                items(wallets.value) { wallet ->
                    WalletCard(wallet, onUploadClick = { newPicture ->
                        coroutineScope.launch {
                            val updatedWallet = wallet.copy(walletPicture = newPicture)
                            walletDao.update(updatedWallet)
                        }
                    }, onDeleteClick = {
                        coroutineScope.launch {
                            walletDao.delete(wallet)
                        }
                    })
                    Divider(color = Color.Gray, thickness = 1.dp)
                }
                item {
                    AddWalletButton {
                        showDialog.value = true
                    }
                }
            }
        }
    )

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Add Wallet") },
            text = {
                Column {
                    TextField(
                        value = walletName.value,
                        onValueChange = { walletName.value = it },
                        label = { Text("Wallet Name") }
                    )
                    Button(onClick = {
                        // Handle picture upload
                        walletPicture.value = "new_picture_path" // Replace with actual picture path
                    }) {
                        Text("Upload Picture")
                    }
                    Button(onClick = {
                        colorPickerDialogState.show()
                    }) {
                        Text("Pick Color")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        walletDao.insert(
                            Wallet(
                                owner = walletName.value,
                                amountPaid = 0.0,
                                amountOwe = 0.0,
                                walletPicture = walletPicture.value,
                                walletColor = tempWalletColor.value.toArgb() // Use temporary color
                            )
                        )
                    }
                    walletColor.value = tempWalletColor.value // Set the selected color
                    showDialog.value = false
                }) {
                    Text("Done")
                }
            }
        )
    }

    MaterialDialog(
        dialogState = colorPickerDialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
            }
        ) {
        colorChooser(
            colors = ColorPalette.Primary,
            argbPickerState = ARGBPickerState.WithAlphaSelector,
            onColorSelected = { color ->
                tempWalletColor.value = color // Save the color temporarily
            }
        )
    }
}

@Composable
fun WalletCard(wallet: Wallet, onUploadClick: (String) -> Unit, onDeleteClick: () -> Unit) {
    val context = LocalContext.current
    val walletPictureResId = context.resources.getIdentifier(wallet.walletPicture, "drawable", context.packageName)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp) // Adjust the height here
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Load and display wallet picture
            val painter = rememberImagePainter(data = walletPictureResId)
            Image(
                painter = painter,
                contentDescription = "Wallet Picture",
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(wallet.owner)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Button(onClick = {
                    // Call onUploadClick with newPicture
                    val newPicture = "new_picture_path" // Replace with actual picture path
                    onUploadClick(newPicture)
                }) {
                    Text("Upload Picture")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onDeleteClick) {
                    Text("Delete Wallet")
                }
            }
        }
    }
}

@Composable
fun AddWalletButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Icon(
            painter = painterResource(id = android.R.drawable.ic_input_add),
            contentDescription = "Add Wallet",
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Add Wallet")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditWalletUI() {
    val navController = rememberNavController()
    EditWalletUI(navController)
}
