@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.kiracash

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.kiracash.model.Item
import com.example.kiracash.model.PaidItem
import com.example.kiracash.model.Receipt
import com.example.kiracash.model.ReceiptItemJoin
import com.example.kiracash.model.Wallet
import com.example.kiracash.model.WalletItemJoin
import com.example.kiracash.ui.theme.KiraCashTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val tag = "DatabaseOperations"
        super.onCreate(savedInstanceState)
        setContent {
            KiraCashTheme {
                NavGraph()
            }
        }

        val db = AppDatabase.getDatabase(this)

        CoroutineScope(Dispatchers.IO).launch {
            val itemDao = db.itemDao()
            val receiptDao = db.receiptDao()
            val walletDao = db.walletDao()
            val receiptItemJoinDao = db.receiptItemJoinDao()
            val walletItemJoinDao = db.walletItemJoinDao()
            val paidItemDao = db.paidItemDao()

            // Delete all data in the database
            db.clearAllTables()

            // Create Wallet objects
            val wallets = listOf(
                Wallet(owner = "John Doe", amountPaid = 0.0, amountOwe = 0.0, walletPicture = "proficon", walletColor = 0xFF0000),
                Wallet(owner = "Jane Doe", amountPaid = 0.0, amountOwe = 0.0, walletPicture = "proficon", walletColor = 0x00FF00),
                Wallet(owner = "John Smith", amountPaid = 0.0, amountOwe = 0.0, walletPicture = "proficon", walletColor = 0x0000FF),
                Wallet(owner = "Myself", amountPaid = 0.0, amountOwe = 0.0, walletPicture = "proficon", walletColor = 0xFFFF00),
            )

            // Insert wallets into the database
            wallets.forEach { walletDao.insert(it) }

            // Retrieve the inserted wallets to get their IDs
            val insertedWallets = walletDao.getAllWallets().first()

            // Create Item objects
            val items = listOf(
                Item(name = "L3 12 LAKSA MEDIUM", price = 12.00),
                Item(name = "L3 12 LAKSA MEDIUM", price = 12.00),
                Item(name = "L3 12 LAKSA MEDIUM", price = 12.00),
                Item(name = "L1 9 LAKSA NORMAL", price = 9.00),
                Item(name = "L1 9 LAKSA NORMAL", price = 9.00),
                Item(name = "TEA O PENG", price = 2.50),
                Item(name = "TEA O PENG", price = 2.50),
                Item(name = "TEA TARIK PENG", price = 3.20),
                Item(name = "KOPI TARIK PENG", price = 3.20),
                Item(name = "AIR SEJUK", price = 0.30)
            )

            // Insert items into the database
            itemDao.insertAll(items)

            // Create PaidItem objects with half isPaid=true and half isPaid=false
            val paidItems = items.mapIndexed { index, item ->
                val isPaid = index < items.size / 2
                val walletId = insertedWallets[index % insertedWallets.size].id
                val paidItem = PaidItem(name = item.name, price = item.price, isPaid = isPaid, walletId = walletId)
                paidItem
            }

            // Insert paid items into the database
            paidItemDao.insertAll(paidItems)

            // Retrieve the inserted items to get their IDs
            val insertedItems = itemDao.getAll()

            // Create a single Receipt object
            val receipt = Receipt()

            // Insert the receipt into the database
            val receiptId = receiptDao.insert(receipt)

            // Create ReceiptItemJoin objects to link each item to the receipt
            val receiptItemJoins = insertedItems.map { item ->
                ReceiptItemJoin(receiptId = receiptId.toInt(), itemId = item.id)
            }

            // Insert joins into the database
            receiptItemJoins.forEach { receiptItemJoinDao.insert(it) }

            // Create WalletItemJoin objects to link each item to its corresponding wallet
            val walletItemJoins = insertedItems.mapIndexed { index, item ->
                WalletItemJoin(walletId = insertedWallets[index % insertedWallets.size].id, itemId = item.id)
            }

            // Insert joins into the database
            walletItemJoins.forEach { walletItemJoinDao.insert(it) }

            // Update each wallet with method in WalletDao
            walletDao.getWalletsWithTotalAmountPaid().collect { updatedWallets ->
                Log.d(tag, "Wallets with total amount paid: $updatedWallets")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val mockNavController = rememberNavController()
    HomeScreen(navController = mockNavController)
}

@Composable
fun HomeScreen(navController: NavHostController) {
    KiraCashTheme {
        val context = LocalContext.current
        val db = AppDatabase.getDatabase(context)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "KiraCash",
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
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                CardSection()

                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "People",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                PersonSection().PersonSectionContent()

                Spacer(modifier = Modifier.height(100.dp))
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Quick Action",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                QuickActionsRow()
            }
        }
    }
}
