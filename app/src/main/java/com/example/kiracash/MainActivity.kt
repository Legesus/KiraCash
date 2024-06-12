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
            val paidItemDao = db.paidItemDao()  // New DAO for PaidItem

            // Delete all data in the database
            db.clearAllTables()

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

            // Create PaidItem objects with half isPaid=true and half isPaid=false
            val paidItems = items.mapIndexed { index, item ->
                val isPaid = index < items.size / 2
                val paidItem = PaidItem(name = item.name, price = item.price, isPaid = isPaid)
                Log.d(tag, "PaidItem: $paidItem, isPaid: $isPaid")
                paidItem
            }

            // Insert items into the database
            itemDao.insertAll(items)
            Log.d(tag, "Inserted items: $items")

            // Insert paid items into the database
            paidItemDao.insertAll(paidItems)
            Log.d(tag, "Inserted paid items: $paidItems")

            // Retrieve the inserted items to get their IDs
            val insertedItems = itemDao.getAll()
            Log.d(tag, "Retrieved items: $insertedItems")

            // Retrieve the inserted paid items to get their IDs
            val insertedPaidItems = paidItemDao.getAllPaidItems().first()
            Log.d(tag, "Retrieved paid items: $insertedPaidItems")

            // Create a single Receipt object
            val receipt = Receipt()

            // Insert the receipt into the database
            val receiptId = receiptDao.insert(receipt)
            Log.d(tag, "Inserted receipt ID: $receipt")

            // Create Wallet objects
            val wallets = listOf(
                Wallet(owner = "John Doe", amountPaid = 0.0, amountOwe = 0.0, walletPicture = "proficon", walletColor = 0xFF0000),
                Wallet(owner = "Jane Doe", amountPaid = 0.0, amountOwe = 0.0, walletPicture = "proficon", walletColor = 0x00FF00),
                Wallet(owner = "John Smith", amountPaid = 0.0, amountOwe = 0.0, walletPicture = "proficon", walletColor = 0x0000FF),
                Wallet(owner = "Myself", amountPaid = 0.0, amountOwe = 0.0, walletPicture = "proficon", walletColor = 0xFFFF00),
            )

            // Insert wallets into the database
            wallets.forEach { walletDao.insert(it) }
            Log.d(tag, "Inserted wallets: $wallets")

            // Retrieve the inserted wallets to get their IDs
            val insertedWallets = walletDao.getAllWallets().first()
            Log.d(tag, "Retrieved wallets: $insertedWallets")

            // Create ReceiptItemJoin objects to link each item to the receipt
            val receiptItemJoins = insertedItems.map { item ->
                ReceiptItemJoin(receiptId = receiptId.toInt(), itemId = item.id)
            }
            Log.d(tag, "ReceiptItemJoins: $receiptItemJoins")

            // Insert joins into the database
            receiptItemJoins.forEach { receiptItemJoinDao.insert(it) }
            Log.d(tag, "Inserted receiptItemJoins: $receiptItemJoins")

            // Create WalletItemJoin objects to link each item to its corresponding wallet
            val johnDoeItems = listOf(insertedItems[0], insertedItems[5], insertedItems[7])
            val janeDoeItems = listOf(insertedItems[1], insertedItems[3], insertedItems[9])
            val johnSmithItems = listOf(insertedItems[2], insertedItems[4], insertedItems[6], insertedItems[8])

            val walletItemJoins = mutableListOf<WalletItemJoin>()

            johnDoeItems.forEach { item ->
                walletItemJoins.add(WalletItemJoin(walletId = insertedWallets[0].id, itemId = item.id))
            }
            janeDoeItems.forEach { item ->
                walletItemJoins.add(WalletItemJoin(walletId = insertedWallets[1].id, itemId = item.id))
            }
            johnSmithItems.forEach { item ->
                walletItemJoins.add(WalletItemJoin(walletId = insertedWallets[2].id, itemId = item.id))
            }

            // Insert joins into the database
            walletItemJoins.forEach { walletItemJoinDao.insert(it) }
            Log.d(tag, "WalletItemJoins: $walletItemJoins")

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
                Spacer(modifier = Modifier.height(10.dp))

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
