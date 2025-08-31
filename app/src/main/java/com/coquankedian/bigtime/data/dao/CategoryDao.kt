package com.coquankedian.bigtime.data.dao

import androidx.room.*
import com.coquankedian.bigtime.data.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY isDefault DESC, name ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE isDefault = 1 ORDER BY name ASC")
    fun getDefaultCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE isCustom = 1 ORDER BY name ASC")
    fun getCustomCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("DELETE FROM categories WHERE id = :categoryId AND isDefault = 0")
    suspend fun deleteCustomCategory(categoryId: Long): Int

    @Query("SELECT COUNT(*) FROM categories WHERE isCustom = 1")
    fun getCustomCategoriesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM categories")
    fun getTotalCategoriesCount(): Flow<Int>
}
