package com.dzug.financetracker.domain.model

data class ExpenseCategory(
    val id: Long,
    val name: String,
    val icon: String,
    val color: Long,
    val budget: Double?,
    val subcategories: List<Subcategory> = emptyList()
)
