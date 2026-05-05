package com.dzug.financetracker.domain.model

import java.time.LocalDate

data class Transaction(
    val id: Long,
    val type: TransactionType,
    val amount: Double,
    val date: LocalDate,
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val subcategoryId: Long?,
    val subcategoryName: String?,
    val note: String?,
    val month: String
)
