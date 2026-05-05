package com.dzug.financetracker.domain.model

data class BudgetStatus(
    val category: ExpenseCategory,
    val spent: Double,
    val remaining: Double,
    val percentage: Float
) {
    val hasBudget: Boolean get() = category.budget != null
    val isOverBudget: Boolean get() = hasBudget && spent > (category.budget ?: 0.0)
}
