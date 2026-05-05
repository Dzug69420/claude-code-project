package com.dzug.financetracker.data.db.dao

import androidx.room.*
import com.dzug.financetracker.data.db.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

data class CategorySpending(
    val categoryId: Long,
    val total: Double
)

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE month = :month ORDER BY date DESC")
    fun getByMonth(month: String): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE month = :month AND type = 'INCOME'")
    fun getTotalIncomeForMonth(month: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE month = :month AND type = 'EXPENSE'")
    fun getTotalExpensesForMonth(month: String): Flow<Double?>

    @Query("""
        SELECT categoryId, SUM(amount) AS total
        FROM transactions
        WHERE month = :month AND type = 'EXPENSE'
        GROUP BY categoryId
    """)
    fun getCategorySpendingForMonth(month: String): Flow<List<CategorySpending>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?
}
