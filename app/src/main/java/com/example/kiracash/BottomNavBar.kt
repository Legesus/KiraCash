package com.example.kiracash

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.Scanner
import androidx.compose.material.icons.rounded.SettingsOverscan
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kiracash.data.BottomNav

val items = listOf(
    BottomNav("Home", Icons.Rounded.Home),
    BottomNav("Wallet", Icons.Rounded.Wallet),
    BottomNav("Receipt", Icons.Rounded.QrCode),
    BottomNav("Monitoring", Icons.Rounded.BarChart),
    BottomNav("Account", Icons.Rounded.AccountCircle)
)


@Composable
fun BottomNavBar(navController: NavHostController) {
    NavigationBar {
        Row (
            modifier = Modifier.background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = index == 0,
                    onClick = {
                        if (item.title == "Wallet") {
                            navController.navigate(MainDestinations.DEBT_MENU_ROUTE)
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewBottomNavBar() {
    // Create a mock NavController
    val mockNavController = rememberNavController()

    BottomNavBar(navController = mockNavController)
}