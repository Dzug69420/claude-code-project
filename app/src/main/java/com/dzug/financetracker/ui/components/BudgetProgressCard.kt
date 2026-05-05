package com.dzug.financetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dzug.financetracker.domain.model.BudgetStatus
import com.dzug.financetracker.ui.theme.BudgetOk
import com.dzug.financetracker.ui.theme.BudgetOver
import com.dzug.financetracker.ui.theme.BudgetWarning

@Composable
fun BudgetProgressCard(
    status: BudgetStatus,
    onEditClick: (() -> Unit)? = null
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${status.category.icon} ${status.category.name}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (status.hasBudget) {
                        val color = when {
                            status.isOverBudget -> BudgetOver
                            status.percentage > 0.8f -> BudgetWarning
                            else -> BudgetOk
                        }
                        Text(
                            text = if (status.isOverBudget)
                                "Over by ${"%.2f".format(-status.remaining)}"
                            else
                                "${"%.2f".format(status.remaining)} left",
                            style = MaterialTheme.typography.labelMedium,
                            color = color
                        )
                    }
                    onEditClick?.let {
                        IconButton(onClick = it, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Set budget", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            if (status.hasBudget) {
                val progressColor = when {
                    status.isOverBudget -> BudgetOver
                    status.percentage > 0.8f -> BudgetWarning
                    else -> BudgetOk
                }
                LinearProgressIndicator(
                    progress = { status.percentage },
                    modifier = Modifier.fillMaxWidth(),
                    color = progressColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Spent: ${"%.2f".format(status.spent)}", style = MaterialTheme.typography.labelSmall)
                    Text("Budget: ${"%.2f".format(status.category.budget)}", style = MaterialTheme.typography.labelSmall)
                }
            } else {
                Text(
                    "Spent: ${"%.2f".format(status.spent)} — no budget set",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
