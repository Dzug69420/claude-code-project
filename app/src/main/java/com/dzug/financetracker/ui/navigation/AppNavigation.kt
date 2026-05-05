package com.dzug.financetracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dzug.financetracker.ui.budget.BudgetScreen
import com.dzug.financetracker.ui.categories.CategoriesScreen
import com.dzug.financetracker.ui.home.HomeScreen
import com.dzug.financetracker.ui.transactions.AddEditTransactionScreen
import com.dzug.financetracker.ui.transactions.TransactionListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDest?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Home.route, Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Transactions.route) {
                TransactionListScreen(onAddClick = { navController.navigate(Screen.AddTransaction.route) })
            }
            composable(Screen.AddTransaction.route) {
                AddEditTransactionScreen(onDone = { navController.popBackStack() })
            }
            composable(Screen.Budget.route) { BudgetScreen() }
            composable(Screen.Categories.route) { CategoriesScreen() }
        }
    }
}
