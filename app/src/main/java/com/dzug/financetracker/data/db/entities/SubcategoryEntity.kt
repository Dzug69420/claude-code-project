package com.dzug.financetracker.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.dzug.financetracker.domain.model.Subcategory

@Entity(
    tableName = "subcategories",
    foreignKeys = [ForeignKey(
        entity = ExpenseCategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("categoryId")]
)
data class SubcategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val categoryId: Long
) {
    fun toDomain() = Subcategory(id = id, name = name, categoryId = categoryId)
}

fun Subcategory.toEntity() = SubcategoryEntity(id = id, name = name, categoryId = categoryId)
