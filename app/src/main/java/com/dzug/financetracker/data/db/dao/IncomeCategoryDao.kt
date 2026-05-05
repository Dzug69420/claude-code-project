package com.dzug.financetracker.data.db.dao

import androidx.room.*
import com.dzug.financetracker.data.db.entities.IncomeCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeCategoryDao {

    @Query("SELECT * FROM income_categories ORDER BY name ASC")
    fun getAll(): Flow<List<IncomeCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: IncomeCategoryEntity): Long

    @Update
    suspend fun update(category: IncomeCategoryEntity)

    @Delete
    suspend fun delete(category: IncomeCategoryEntity)

    @Query("SELECT COUNT(*) FROM income_categories")
    suspend fun count(): Int
}
