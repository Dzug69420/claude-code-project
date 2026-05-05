package com.dzug.financetracker.data.db.dao

import androidx.room.*
import com.dzug.financetracker.data.db.entities.ExpenseCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseCategoryDao {

    @Query("SELECT * FROM expense_categories ORDER BY name ASC")
    fun getAll(): Flow<List<ExpenseCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: ExpenseCategoryEntity): Long

    @Update
    suspend fun update(category: ExpenseCategoryEntity)

    @Delete
    suspend fun delete(category: ExpenseCategoryEntity)

    @Query("SELECT COUNT(*) FROM expense_categories")
    suspend fun count(): Int
}
