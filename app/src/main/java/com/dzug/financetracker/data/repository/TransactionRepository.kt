package com.dzug.financetracker.data.repository

import com.dzug.financetracker.data.db.dao.ExpenseCategoryDao
import com.dzug.financetracker.data.db.dao.IncomeCategoryDao
import com.dzug.financetracker.data.db.dao.SubcategoryDao
import com.dzug.financetracker.data.db.dao.TransactionDao
import com.dzug.financetracker.data.db.entities.TransactionEntity
import com.dzug.financetracker.domain.model.Transaction
import com.dzug.financetracker.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val expenseCategoryDao: ExpenseCategoryDao,
    private val subcategoryDao: SubcategoryDao,
    private val incomeCategoryDao: IncomeCategoryDao
) {
    private val monthFmt = DateTimeFormatter.ofPattern("yyyy-MM")

    fun getTransactionsForMonth(month: String): Flow<List<Transaction>> =
        combine(
            transactionDao.getByMonth(month),
            expenseCategoryDao.getAll(),
            subcategoryDao.getAll(),
            incomeCategoryDao.getAll()
        ) { txns, expCats, subs, incCats ->
            txns.map { txn ->
                val type = TransactionType.valueOf(txn.type)
                val expCat = expCats.find { it.id == txn.categoryId }
                val incCat = incCats.find { it.id == txn.categoryId }
                val sub = subs.find { it.id == txn.subcategoryId }
                Transaction(
                    id = txn.id,
                    type = type,
                    amount = txn.amount,
                    date = LocalDate.ofEpochDay(txn.date),
                    categoryId = txn.categoryId,
                    categoryName = expCat?.name ?: incCat?.name ?: "Unknown",
                    categoryIcon = expCat?.icon ?: incCat?.icon ?: "❓",
                    subcategoryId = txn.subcategoryId,
                    subcategoryName = sub?.name,
                    note = txn.note,
                    month = txn.month
                )
            }
        }

    fun getTotalIncome(month: String): Flow<Double?> =
        transactionDao.getTotalIncomeForMonth(month)

    fun getTotalExpenses(month: String): Flow<Double?> =
        transactionDao.getTotalExpensesForMonth(month)

    fun getCategorySpendingMap(month: String): Flow<Map<Long, Double>> =
        transactionDao.getCategorySpendingForMonth(month)
            .map { list -> list.associate { it.categoryId to it.total } }

    suspend fun addTransaction(
        type: TransactionType,
        amount: Double,
        date: LocalDate,
        categoryId: Long,
        subcategoryId: Long?,
        note: String?
    ) {
        transactionDao.insert(
            TransactionEntity(
                type = type.name,
                amount = amount,
                date = date.toEpochDay(),
                categoryId = categoryId,
                subcategoryId = subcategoryId,
                note = note?.ifBlank { null },
                month = date.format(monthFmt)
            )
        )
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(
            TransactionEntity(
                id = transaction.id,
                type = transaction.type.name,
                amount = transaction.amount,
                date = transaction.date.toEpochDay(),
                categoryId = transaction.categoryId,
                subcategoryId = transaction.subcategoryId,
                note = transaction.note,
                month = transaction.month
            )
        )
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(
            TransactionEntity(
                id = transaction.id,
                type = transaction.type.name,
                amount = transaction.amount,
                date = transaction.date.toEpochDay(),
                categoryId = transaction.categoryId,
                subcategoryId = transaction.subcategoryId,
                note = transaction.note,
                month = transaction.month
            )
        )
    }

    suspend fun getById(id: Long): TransactionEntity? = transactionDao.getById(id)
}
