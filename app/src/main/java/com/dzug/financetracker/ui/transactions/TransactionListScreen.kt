package com.dzug.financetracker.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dzug.financetracker.domain.model.Transaction
import com.dzug.financetracker.ui.components.MonthSelector
import com.dzug.financetracker.ui.components.TransactionItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TransactionListScreen(
    onAddClick: () -> Unit,
    vm: TransactionViewModel = hiltViewModel()
) {
    val state by vm.listState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add transaction")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            MonthSelector(
                month = state.month,
                onPrevious = vm::previousMonth,
                onNext = vm::nextMonth,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (state.transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No transactions this month", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                val grouped = state.transactions.groupBy { it.date }
                LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                    grouped.entries.sortedByDescending { it.key }.forEach { (date, txns) ->
                        item {
                            Text(
                                text = date.format(DateTimeFormatter.ofPattern("EEE, MMM d")),
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                        items(txns, key = { it.id }) { txn ->
                            TransactionItem(
                                transaction = txn,
                                onDelete = { vm.deleteTransaction(txn) }
                            )
                        }
                    }
                }
            }
        }
    }
}
