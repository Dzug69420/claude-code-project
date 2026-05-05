package com.dzug.financetracker.data.repository

import com.dzug.financetracker.data.db.dao.ExpenseCategoryDao
import com.dzug.financetracker.data.db.dao.IncomeCategoryDao
import com.dzug.financetracker.data.db.dao.SubcategoryDao
import com.dzug.financetracker.data.db.entities.ExpenseCategoryEntity
import com.dzug.financetracker.data.db.entities.IncomeCategoryEntity
import com.dzug.financetracker.data.db.entities.SubcategoryEntity
import com.dzug.financetracker.data.db.entities.toEntity
import com.dzug.financetracker.domain.model.ExpenseCategory
import com.dzug.financetracker.domain.model.IncomeCategory
import com.dzug.financetracker.domain.model.Subcategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val expenseCategoryDao: ExpenseCategoryDao,
    private val subcategoryDao: SubcategoryDao,
    private val incomeCategoryDao: IncomeCategoryDao
) {
    fun getExpenseCategories(): Flow<List<ExpenseCategory>> =
        combine(expenseCategoryDao.getAll(), subcategoryDao.getAll()) { cats, subs ->
            cats.map { cat ->
                cat.toDomain().copy(
                    subcategories = subs.filter { it.categoryId == cat.id }.map { it.toDomain() }
                )
            }
        }

    fun getExpenseCategoriesFlat(): Flow<List<ExpenseCategory>> =
        expenseCategoryDao.getAll().map { it.map { e -> e.toDomain() } }

    fun getSubcategoriesForCategory(categoryId: Long): Flow<List<Subcategory>> =
        subcategoryDao.getByCategoryId(categoryId).map { it.map { s -> s.toDomain() } }

    fun getIncomeCategories(): Flow<List<IncomeCategory>> =
        incomeCategoryDao.getAll().map { it.map { e -> e.toDomain() } }

    suspend fun addExpenseCategory(name: String, icon: String, color: Long, budget: Double?): Long =
        expenseCategoryDao.insert(ExpenseCategoryEntity(name = name, icon = icon, color = color, budget = budget))

    suspend fun updateExpenseCategory(category: ExpenseCategory) =
        expenseCategoryDao.update(category.toEntity())

    suspend fun deleteExpenseCategory(category: ExpenseCategory) =
        expenseCategoryDao.delete(category.toEntity())

    suspend fun addSubcategory(name: String, categoryId: Long): Long =
        subcategoryDao.insert(SubcategoryEntity(name = name, categoryId = categoryId))

    suspend fun updateSubcategory(sub: Subcategory) =
        subcategoryDao.update(sub.toEntity())

    suspend fun deleteSubcategory(sub: Subcategory) =
        subcategoryDao.delete(sub.toEntity())

    suspend fun addIncomeCategory(name: String, icon: String, color: Long): Long =
        incomeCategoryDao.insert(IncomeCategoryEntity(name = name, icon = icon, color = color))

    suspend fun updateIncomeCategory(category: IncomeCategory) =
        incomeCategoryDao.update(category.toEntity())

    suspend fun deleteIncomeCategory(category: IncomeCategory) =
        incomeCategoryDao.delete(category.toEntity())
}
