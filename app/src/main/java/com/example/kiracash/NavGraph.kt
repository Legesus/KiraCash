package com.example.kiracash

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object MainDestinations {
    const val HOME_ROUTE = "home"
    const val DEBT_MENU_ROUTE = "debtMenu"
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
    }
}