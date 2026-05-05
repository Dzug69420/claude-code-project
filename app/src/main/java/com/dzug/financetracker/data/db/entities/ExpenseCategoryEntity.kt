package com.dzug.financetracker.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dzug.financetracker.domain.model.ExpenseCategory

@Entity(tableName = "expense_categories")
data class ExpenseCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String,
    val color: Long,
    val budget: Double? = null
) {
    fun toDomain() = ExpenseCategory(id = id, name = name, icon = icon, color = color, budget = budget)
}

fun ExpenseCategory.toEntity() = ExpenseCategoryEntity(id = id, name = name, icon = icon, color = color, budget = budget)
