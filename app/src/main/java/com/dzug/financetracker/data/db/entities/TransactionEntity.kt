package com.dzug.financetracker.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val amount: Double,
    val date: Long,
    val categoryId: Long,
    val subcategoryId: Long? = null,
    val note: String? = null,
    val month: String
)
