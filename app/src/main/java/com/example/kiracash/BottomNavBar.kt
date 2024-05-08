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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kiracash.data.BottomNav

@Composable
fun NavHostController.currentRoute(): String? {
    val navBackStackEntry = this.currentBackStackEntryAsState().value
    return navBackStackEntry?.destination?.route
}

data class BottomNav(
    val title: String,
    val icon: ImageVector,
    val route: String
)

val items = listOf(
    BottomNav("Home", Icons.Rounded.Home, MainDestinations.HOME_ROUTE),
    BottomNav("Wallet", Icons.Rounded.Wallet, MainDestinations.DEBT_MENU_ROUTE),
    BottomNav("Receipt", Icons.Rounded.QrCode, MainDestinations.QR_MENU_ROUTE),
    BottomNav("Statistic", Icons.Rounded.BarChart, MainDestinations.STATISTIC_SCREEN_ROUTE),
    BottomNav("Account", Icons.Rounded.AccountCircle, MainDestinations.ACCOUNT_ROUTE)
)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val currentRoute = navController.currentRoute()
    NavigationBar {
        Row (
            modifier = Modifier.background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
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