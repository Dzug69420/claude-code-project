package com.dzug.financetracker.ui.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dzug.financetracker.domain.model.ExpenseCategory
import com.dzug.financetracker.domain.model.IncomeCategory

@Composable
fun CategoriesScreen(vm: CategoriesViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var showAddExpense by remember { mutableStateOf(false) }
    var showAddIncome by remember { mutableStateOf(false) }
    var expandedCategoryId by remember { mutableStateOf<Long?>(null) }
    var addSubcategoryFor by remember { mutableStateOf<ExpenseCategory?>(null) }

    if (showAddExpense) {
        AddCategoryDialog(
            title = "Add Expense Category",
            onDismiss = { showAddExpense = false },
            onConfirm = { name, icon -> vm.addExpenseCategory(name, icon, 0xFFE57373, null); showAddExpense = false }
        )
    }
    if (showAddIncome) {
        AddCategoryDialog(
            title = "Add Income Category",
            onDismiss = { showAddIncome = false },
            onConfirm = { name, icon -> vm.addIncomeCategory(name, icon, 0xFF66BB6A); showAddIncome = false }
        )
    }
    addSubcategoryFor?.let { cat ->
        AddNameDialog(
            title = "Add Subcategory to ${cat.name}",
            onDismiss = { addSubcategoryFor = null },
            onConfirm = { name -> vm.addSubcategory(name, cat.id); addSubcategoryFor = null }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Expense Categories", style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { showAddExpense = true }) { Icon(Icons.Default.Add, null) }
            }
        }

        items(state.expenseCategories, key = { it.id }) { cat ->
            val isExpanded = expandedCategoryId == cat.id
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${cat.icon} ${cat.name}", style = MaterialTheme.typography.bodyLarge)
                        Row {
                            IconButton(onClick = { expandedCategoryId = if (isExpanded) null else cat.id }) {
                                Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
                            }
                            IconButton(onClick = { vm.deleteExpenseCategory(cat) }) {
                                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                    if (isExpanded) {
                        cat.subcategories.forEach { sub ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("• ${sub.name}", style = MaterialTheme.typography.bodyMedium)
                                IconButton(onClick = { vm.deleteSubcategory(sub) }) {
                                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        TextButton(onClick = { addSubcategoryFor = cat }) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Add Subcategory")
                        }
                    }
                }
            }
        }

        item {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Income Categories", style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { showAddIncome = true }) { Icon(Icons.Default.Add, null) }
            }
        }

        items(state.incomeCategories, key = { "inc_${it.id}" }) { cat ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${cat.icon} ${cat.name}", style = MaterialTheme.typography.bodyLarge)
                    IconButton(onClick = { vm.deleteIncomeCategory(cat) }) {
                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddCategoryDialog(title: String, onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("📁") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true)
                OutlinedTextField(value = icon, onValueChange = { icon = it }, label = { Text("Icon (emoji)") }, singleLine = true)
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(name, icon) }, enabled = name.isNotBlank()) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun AddNameDialog(title: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true) },
        confirmButton = { TextButton(onClick = { onConfirm(name) }, enabled = name.isNotBlank()) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
