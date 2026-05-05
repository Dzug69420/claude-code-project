package com.dzug.financetracker.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dzug.financetracker.ui.components.BudgetProgressCard
import com.dzug.financetracker.ui.components.MonthSelector
import com.dzug.financetracker.ui.theme.ExpenseRed
import com.dzug.financetracker.ui.theme.IncomeGreen

@Composable
fun HomeScreen(vm: HomeViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MonthSelector(
                month = state.month,
                onPrevious = vm::previousMonth,
                onNext = vm::nextMonth
            )
        }

        item { SummaryCard(state) }

        if (state.budgetStatuses.isNotEmpty()) {
            item {
                Text(
                    "Budget Overview",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(state.budgetStatuses) { status ->
                BudgetProgressCard(status)
            }
        }
    }
}

@Composable
private fun SummaryCard(state: HomeUiState) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Monthly Summary", style = MaterialTheme.typography.titleMedium)
            Divider()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text("Income", style = MaterialTheme.typography.labelMedium)
                    Text(
                        "+${"%.2f".format(state.totalIncome)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = IncomeGreen
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Balance", style = MaterialTheme.typography.labelMedium)
                    val balanceColor = if (state.netBalance >= 0) IncomeGreen else ExpenseRed
                    Text(
                        "${"%.2f".format(state.netBalance)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = balanceColor
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Expenses", style = MaterialTheme.typography.labelMedium)
                    Text(
                        "-${"%.2f".format(state.totalExpenses)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ExpenseRed
                    )
                }
            }
        }
    }
}
