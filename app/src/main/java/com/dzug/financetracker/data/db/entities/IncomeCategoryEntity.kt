package com.dzug.financetracker.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dzug.financetracker.domain.model.IncomeCategory

@Entity(tableName = "income_categories")
data class IncomeCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String,
    val color: Long
) {
    fun toDomain() = IncomeCategory(id = id, name = name, icon = icon, color = color)
}

fun IncomeCategory.toEntity() = IncomeCategoryEntity(id = id, name = name, icon = icon, color = color)
