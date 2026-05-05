package com.dzug.financetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dzug.financetracker.domain.model.Transaction
import com.dzug.financetracker.domain.model.TransactionType
import com.dzug.financetracker.ui.theme.ExpenseRed
import com.dzug.financetracker.ui.theme.IncomeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItem(
    transaction: Transaction,
    onDelete: () -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Delete transaction?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showConfirm = false }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("Cancel") } }
        )
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                showConfirm = true
            }
            false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text("Delete", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge)
            }
        },
        enableDismissFromStartToEnd = false
    ) {
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = buildString {
                            append(transaction.categoryIcon)
                            append(" ")
                            append(transaction.categoryName)
                            transaction.subcategoryName?.let { append(" › $it") }
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    transaction.note?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                val color = if (transaction.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
                val sign = if (transaction.type == TransactionType.INCOME) "+" else "-"
                Text(
                    "$sign${"%.2f".format(transaction.amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = color
                )
            }
        }
    }
}
