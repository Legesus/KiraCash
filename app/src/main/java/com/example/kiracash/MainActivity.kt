package com.example.kiracash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.kiracash.ui.theme.KiraCashTheme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

//import org.jetbrains.annotations.ApiStatus.Experimental

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KiraCashTheme {
                NavGraph()
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

                CardSection()
                Spacer(modifier = Modifier.height(10.dp))

                val people = listOf(
                    Person(
                        profilePicture = "person_icon", // Using person icon from extended icons library
                        debtAmount = 100.0,
                        cashIn = 200.0,
                        cashOut = 50.0
                    ),
                    Person(
                        profilePicture = "person_icon", // Using person icon from extended icons library
                        debtAmount = 200.0,
                        cashIn = 300.0,
                        cashOut = 150.0
                    ),
                    Person(
                        profilePicture = "person_icon", // Using person icon from extended icons library
                        debtAmount = 300.0,
                        cashIn = 400.0,
                        cashOut = 250.0
                    ),
                )

                PersonSection().PersonSectionContent(people)

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


