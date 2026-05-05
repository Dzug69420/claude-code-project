package com.dzug.financetracker.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dzug.financetracker.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    onDone: () -> Unit,
    vm: TransactionViewModel = hiltViewModel()
) {
    val state by vm.formState.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) {
            vm.resetForm()
            onDone()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Type toggle
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TransactionType.values().forEach { type ->
                    FilterChip(
                        selected = state.type == type,
                        onClick = { vm.setType(type) },
                        label = { Text(type.name.lowercase().replaceFirstChar { it.uppercaseChar() }) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Amount
            OutlinedTextField(
                value = state.amount,
                onValueChange = vm::setAmount,
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Category
            if (state.type == TransactionType.EXPENSE) {
                var expCatExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expCatExpanded,
                    onExpandedChange = { expCatExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.selectedExpenseCategory?.let { "${it.icon} ${it.name}" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expCatExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expCatExpanded, onDismissRequest = { expCatExpanded = false }) {
                        state.expenseCategories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text("${cat.icon} ${cat.name}") },
                                onClick = { vm.setExpenseCategory(cat); expCatExpanded = false }
                            )
                        }
                    }
                }

                // Subcategory
                if (state.subcategories.isNotEmpty()) {
                    var subExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = subExpanded,
                        onExpandedChange = { subExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = state.selectedSubcategory?.name ?: "None",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Subcategory (optional)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = subExpanded, onDismissRequest = { subExpanded = false }) {
                            DropdownMenuItem(text = { Text("None") }, onClick = { vm.setSubcategory(null); subExpanded = false })
                            state.subcategories.forEach { sub ->
                                DropdownMenuItem(
                                    text = { Text(sub.name) },
                                    onClick = { vm.setSubcategory(sub); subExpanded = false }
                                )
                            }
                        }
                    }
                }
            } else {
                var incCatExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = incCatExpanded,
                    onExpandedChange = { incCatExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.selectedIncomeCategory?.let { "${it.icon} ${it.name}" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = incCatExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = incCatExpanded, onDismissRequest = { incCatExpanded = false }) {
                        state.incomeCategories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text("${cat.icon} ${cat.name}") },
                                onClick = { vm.setIncomeCategory(cat); incCatExpanded = false }
                            )
                        }
                    }
                }
            }

            // Note
            OutlinedTextField(
                value = state.note,
                onValueChange = vm::setNote,
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = vm::saveTransaction,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving && state.amount.isNotBlank()
            ) {
                if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("Save")
            }
        }
    }
}
