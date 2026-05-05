package com.dzug.financetracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dzug.financetracker.data.db.dao.ExpenseCategoryDao
import com.dzug.financetracker.data.db.dao.IncomeCategoryDao
import com.dzug.financetracker.data.db.dao.SubcategoryDao
import com.dzug.financetracker.data.db.dao.TransactionDao
import com.dzug.financetracker.data.db.entities.ExpenseCategoryEntity
import com.dzug.financetracker.data.db.entities.IncomeCategoryEntity
import com.dzug.financetracker.data.db.entities.SubcategoryEntity
import com.dzug.financetracker.data.db.entities.TransactionEntity

@Database(
    entities = [
        ExpenseCategoryEntity::class,
        SubcategoryEntity::class,
        IncomeCategoryEntity::class,
        TransactionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseCategoryDao(): ExpenseCategoryDao
    abstract fun subcategoryDao(): SubcategoryDao
    abstract fun incomeCategoryDao(): IncomeCategoryDao
    abstract fun transactionDao(): TransactionDao
}
