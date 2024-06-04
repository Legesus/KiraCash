package com.example.kiracash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.kiracash.model.Wallet
import com.example.kiracash.ui.theme.KiraCashTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//import org.jetbrains.annotations.ApiStatus.Experimental

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KiraCashTheme {
                NavGraph()
            }
        }

        val db = AppDatabase.getDatabase(this)
        val walletDao = db.walletDao()

        CoroutineScope(Dispatchers.IO).launch {

            // Clear all tables
            db.clearAllTables()

            val existingWallets = walletDao.getAllWallets().map { it.owner }

            if (!existingWallets.contains("John Doe")) {
                val johnDoeWallet = Wallet(owner = "John Doe", amountPaid = 100.0, amountOwe = 0.0)
                walletDao.insert(johnDoeWallet)
            }

            if (!existingWallets.contains("Jane Doe")) {
                val janeDoeWallet = Wallet(owner = "Jane Doe", amountPaid = 0.0, amountOwe = 0.0)
                walletDao.insert(janeDoeWallet)
            }

            if (!existingWallets.contains("John Smith")) {
                val johnSmithWallet = Wallet(owner = "John Smith", amountPaid = 0.0, amountOwe = 0.0)
                walletDao.insert(johnSmithWallet)
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
        val walletDao = db.walletDao()

        //Greeting("Adam")
        Scaffold (
            bottomBar = {
                BottomNavBar(navController = navController)
            }
        ) { padding ->

            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Text (
                    modifier = Modifier.padding(16.dp),
                    text = "KiraCash",
                    color = Color.Black,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                // Text saying Overview with sizes that is a subheading to the heading "KiraCash"
                Text (
                    modifier = Modifier.padding(16.dp),
                    text = "Overview",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                CardSection()
                Spacer(modifier = Modifier.height(10.dp))

                val people = listOf(
                    Person(
                        profilePicture = "person_icon", // Using person icon from extended icons library
                        name = "John Doe",
                        cashIn = 200.0,
                        cashOut = 50.0
                    ),
                    Person(
                        profilePicture = "person_icon", // Using person icon from extended icons library
                        name = "Jane Doe",
                        cashIn = 300.0,
                        cashOut = 150.0
                    ),
                    Person(
                        profilePicture = "person_icon", // Using person icon from extended icons library\
                        name = "John Smith",
                        cashIn = 400.0,
                        cashOut = 250.0
                    ),
                )

                PersonSection().PersonSectionContent()

                Spacer(modifier = Modifier.height(100.dp))
                Text (
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


