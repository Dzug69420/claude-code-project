package com.dzug.financetracker.ui.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dzug.financetracker.domain.model.BudgetStatus
import com.dzug.financetracker.ui.components.BudgetProgressCard
import com.dzug.financetracker.ui.components.MonthSelector

@Composable
fun BudgetScreen(vm: BudgetViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var editingStatus by remember { mutableStateOf<BudgetStatus?>(null) }

    editingStatus?.let { status ->
        BudgetEditDialog(
            status = status,
            onDismiss = { editingStatus = null },
            onConfirm = { budget ->
                vm.setBudget(status.category, budget)
                editingStatus = null
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MonthSelector(month = state.month, onPrevious = vm::previousMonth, onNext = vm::nextMonth)
        }
        item { Text("Expense Budgets", style = MaterialTheme.typography.titleMedium) }
        items(state.budgetStatuses) { status ->
            BudgetProgressCard(
                status = status,
                onEditClick = { editingStatus = status }
            )
        }
    }
}

@Composable
private fun BudgetEditDialog(
    status: BudgetStatus,
    onDismiss: () -> Unit,
    onConfirm: (Double?) -> Unit
) {
    var budgetText by remember { mutableStateOf(status.category.budget?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Budget — ${status.category.icon} ${status.category.name}") },
        text = {
            OutlinedTextField(
                value = budgetText,
                onValueChange = { budgetText = it },
                label = { Text("Monthly budget (leave blank to remove)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(budgetText.toDoubleOrNull()) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
