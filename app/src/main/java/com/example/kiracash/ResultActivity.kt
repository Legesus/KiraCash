import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.kiracash.data.ReceiptItem
import com.example.kiracash.data.WalletEntity

class ResultActivity : ComponentActivity() {

    private val viewModel: ResultViewModel by lazy {
        ViewModelProvider(this)[ResultViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recognizedText = intent.getStringExtra("RECOGNIZED_TEXT") ?: ""
        viewModel.parseAndSaveReceiptItems(recognizedText)

        setContent {
            ResultScreen(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(viewModel: ResultViewModel) {
    val wallets: List<WalletEntity> by viewModel.getWallets().observeAsState(initial = emptyList())
    var newWalletName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Assign Items to Wallets") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1F1B24)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = newWalletName,
                onValueChange = { newWalletName = it },
                modifier = Modifier.padding(16.dp),
                placeholder = { Text("New Wallet Name") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface
                )
            )

            Button(
                onClick = { viewModel.insertWallet(newWalletName) },
                modifier = Modifier.padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
            ) {
                Text("Create Wallet", color = Color.White)
            }

            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                items(wallets) { wallet ->
                    WalletItem(wallet, viewModel)
                }
            }
        }
    }
}

@Composable
fun WalletItem(wallet: WalletEntity, viewModel: ResultViewModel) {
    Column {
        Text(text = wallet.walletName, color = Color.White)

        val receiptItems: List<ReceiptItem> by viewModel.getReceiptItems().observeAsState(initial = emptyList())
        receiptItems.forEach { item ->
            Row {
                Text(text = item.itemName, color = Color.White)
                Button(
                    onClick = { viewModel.assignItemToWallet(item.id.toLong(), wallet.id.toLong()) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
                ) {
                    Text("Assign to Wallet", color = Color.White)
                }
            }
        }
    }
}