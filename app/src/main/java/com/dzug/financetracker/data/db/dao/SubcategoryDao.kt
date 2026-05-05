package com.dzug.financetracker.data.db.dao

import androidx.room.*
import com.dzug.financetracker.data.db.entities.SubcategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubcategoryDao {

    @Query("SELECT * FROM subcategories ORDER BY name ASC")
    fun getAll(): Flow<List<SubcategoryEntity>>

    @Query("SELECT * FROM subcategories WHERE categoryId = :categoryId ORDER BY name ASC")
    fun getByCategoryId(categoryId: Long): Flow<List<SubcategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sub: SubcategoryEntity): Long

    @Update
    suspend fun update(sub: SubcategoryEntity)

    @Delete
    suspend fun delete(sub: SubcategoryEntity)
}
