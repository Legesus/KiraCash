package com.example.kiracash

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object MainDestinations {
    const val HOME_ROUTE = "home"
    const val DEBT_MENU_ROUTE = "debtMenu"
    const val STATISTIC_SCREEN_ROUTE = "statisticScreen"
    const val ACCOUNT_ROUTE = "account"
    const val QR_MENU_ROUTE = "qrMenu"
}

@Composable
fun NavGraph(startDestination: String = MainDestinations.HOME_ROUTE) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainDestinations.HOME_ROUTE) {
            HomeScreen(navController = navController)
        }
        composable(MainDestinations.DEBT_MENU_ROUTE) {
            DebtMenuScreen(navController = navController)
        }
        composable(MainDestinations.STATISTIC_SCREEN_ROUTE) {
            StatisticScreen(navController = navController)
        }
        composable(MainDestinations.ACCOUNT_ROUTE) {
            ProfileScreen(navController = navController)
        }
        composable(MainDestinations.QR_MENU_ROUTE) {
            OCRScreen(navController = navController)
        }
    }

}