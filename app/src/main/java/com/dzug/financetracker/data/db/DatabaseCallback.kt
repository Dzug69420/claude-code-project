package com.dzug.financetracker.data.db

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dzug.financetracker.data.db.entities.ExpenseCategoryEntity
import com.dzug.financetracker.data.db.entities.IncomeCategoryEntity
import com.dzug.financetracker.data.db.entities.SubcategoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class DatabaseCallback @Inject constructor(
    private val databaseProvider: Provider<AppDatabase>
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            populateDefaults(databaseProvider.get())
        }
    }

    private suspend fun populateDefaults(database: AppDatabase) {
        val expenseDao = database.expenseCategoryDao()
        val subDao = database.subcategoryDao()
        val incomeDao = database.incomeCategoryDao()

        if (expenseDao.count() > 0) return

        val expenseCategories = listOf(
            ExpenseCategoryEntity(name = "Food & Dining",   icon = "🍔", color = 0xFFE57373, budget = null),
            ExpenseCategoryEntity(name = "Transport",       icon = "🚗", color = 0xFF64B5F6, budget = null),
            ExpenseCategoryEntity(name = "Housing",         icon = "🏠", color = 0xFFFFB74D, budget = null),
            ExpenseCategoryEntity(name = "Health",          icon = "💊", color = 0xFF81C784, budget = null),
            ExpenseCategoryEntity(name = "Entertainment",   icon = "🎮", color = 0xFFBA68C8, budget = null),
            ExpenseCategoryEntity(name = "Shopping",        icon = "👕", color = 0xFFFF8A65, budget = null),
            ExpenseCategoryEntity(name = "Education",       icon = "📚", color = 0xFF4FC3F7, budget = null),
            ExpenseCategoryEntity(name = "Utilities",       icon = "💡", color = 0xFFFFD54F, budget = null)
        )

        val ids = expenseCategories.map { expenseDao.insert(it) }

        val subcategories = listOf(
            SubcategoryEntity(name = "Restaurants",       categoryId = ids[0]),
            SubcategoryEntity(name = "Groceries",         categoryId = ids[0]),
            SubcategoryEntity(name = "Coffee & Snacks",   categoryId = ids[0]),
            SubcategoryEntity(name = "Delivery",          categoryId = ids[0]),
            SubcategoryEntity(name = "Fuel",              categoryId = ids[1]),
            SubcategoryEntity(name = "Public Transport",  categoryId = ids[1]),
            SubcategoryEntity(name = "Parking",           categoryId = ids[1]),
            SubcategoryEntity(name = "Car Maintenance",   categoryId = ids[1]),
            SubcategoryEntity(name = "Rent / Mortgage",   categoryId = ids[2]),
            SubcategoryEntity(name = "Home Insurance",    categoryId = ids[2]),
            SubcategoryEntity(name = "Maintenance",       categoryId = ids[2]),
            SubcategoryEntity(name = "Electricity",       categoryId = ids[7]),
            SubcategoryEntity(name = "Internet",          categoryId = ids[7]),
            SubcategoryEntity(name = "Water",             categoryId = ids[7])
        )
        subcategories.forEach { subDao.insert(it) }

        val incomeCategories = listOf(
            IncomeCategoryEntity(name = "Salary",      icon = "💼", color = 0xFF66BB6A),
            IncomeCategoryEntity(name = "Freelance",   icon = "💻", color = 0xFF26C6DA),
            IncomeCategoryEntity(name = "Investments", icon = "📈", color = 0xFFAB47BC),
            IncomeCategoryEntity(name = "Other",       icon = "🎁", color = 0xFFEF5350)
        )
        incomeCategories.forEach { incomeDao.insert(it) }
    }
}
