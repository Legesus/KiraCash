
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.kiracash.model.AppDatabase
import com.example.kiracash.model.Wallet
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.color.ARGBPickerState
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWalletUI(navController: NavController) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val walletDao = db.walletDao()
    val wallets = walletDao.getAllWallets().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val showDialog = remember { mutableStateOf(false) }
    val walletName = remember { mutableStateOf("") }
    val walletPicture = remember { mutableStateOf("") }
    val walletColor = remember { mutableStateOf(Color.White) }
    val tempWalletColor = remember { mutableStateOf(Color.White) }

    val colorPickerDialogState = rememberMaterialDialogState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val imagePath = saveImageToInternalStorage(context, it)
            walletPicture.value = imagePath
        }
    }

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
                    WalletCard(wallet, galleryLauncher, onUploadClick = { newPicture ->
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
                        galleryLauncher.launch("image/*")
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
                                walletColor = tempWalletColor.value.toArgb()
                            )
                        )
                    }
                    walletColor.value = tempWalletColor.value
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
                tempWalletColor.value = color
            }
        )
    }
}

@Composable
fun WalletCard(wallet: Wallet, galleryLauncher: ActivityResultLauncher<String>, onUploadClick: (String) -> Unit, onDeleteClick: () -> Unit) {
    val context = LocalContext.current
    val walletPictureResId = context.resources.getIdentifier(wallet.walletPicture, "drawable", context.packageName)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (wallet.walletPicture.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeFile(wallet.walletPicture)
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Wallet Picture",
                        modifier = Modifier.size(60.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = walletPictureResId),
                        contentDescription = "Wallet Picture",
                        modifier = Modifier.size(60.dp)
                    )
                }
            } else {
                Image(
                    painter = painterResource(id = walletPictureResId),
                    contentDescription = "Wallet Picture",
                    modifier = Modifier.size(60.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(wallet.owner)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Button(onClick = {
                    galleryLauncher.launch("image/*")
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

// Helper function to save image to internal storage
fun saveImageToInternalStorage(context: Context, imageUri: Uri): String {
    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
    val filename = "${System.currentTimeMillis()}.jpg"
    val file = File(context.filesDir, filename)
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.flush()
    outputStream.close()
    return file.absolutePath
}
