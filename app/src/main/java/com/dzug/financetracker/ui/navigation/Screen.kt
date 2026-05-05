package com.dzug.financetracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home           : Screen("home",            "Home",         Icons.Default.Home)
    object Transactions   : Screen("transactions",    "Transactions", Icons.Default.AccountBalanceWallet)
    object AddTransaction : Screen("add_transaction", "Add",          Icons.Default.Add)
    object Budget         : Screen("budget",          "Budget",       Icons.Default.PieChart)
    object Categories     : Screen("categories",      "Categories",   Icons.Default.Category)
}

val bottomNavItems = listOf(Screen.Home, Screen.Transactions, Screen.Budget, Screen.Categories)
