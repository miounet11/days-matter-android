package com.coquankedian.bigtime.data.dao

import androidx.room.*
import com.coquankedian.bigtime.data.model.Notebook
import kotlinx.coroutines.flow.Flow

@Dao
interface NotebookDao {

    @Query("SELECT * FROM notebooks ORDER BY sortOrder ASC, createdAt DESC")
    fun getAllNotebooks(): Flow<List<Notebook>>

    @Query("SELECT * FROM notebooks WHERE isHidden = 0 ORDER BY sortOrder ASC, createdAt DESC")
    fun getVisibleNotebooks(): Flow<List<Notebook>>

    @Query("SELECT * FROM notebooks WHERE isDefault = 1 ORDER BY sortOrder ASC")
    fun getDefaultNotebooks(): Flow<List<Notebook>>

    @Query("SELECT * FROM notebooks WHERE isDefault = 0 ORDER BY sortOrder ASC, createdAt DESC")
    fun getCustomNotebooks(): Flow<List<Notebook>>

    @Query("SELECT * FROM notebooks WHERE id = :notebookId")
    suspend fun getNotebookById(notebookId: Long): Notebook?

    @Query("SELECT * FROM notebooks WHERE name = :name LIMIT 1")
    suspend fun getNotebookByName(name: String): Notebook?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotebook(notebook: Notebook): Long

    @Update
    suspend fun updateNotebook(notebook: Notebook)

    @Delete
    suspend fun deleteNotebook(notebook: Notebook)

    @Query("DELETE FROM notebooks WHERE id = :notebookId AND isDefault = 0")
    suspend fun deleteCustomNotebook(notebookId: Long): Int

    @Query("UPDATE notebooks SET isHidden = :isHidden WHERE id = :notebookId")
    suspend fun updateNotebookVisibility(notebookId: Long, isHidden: Boolean)

    @Query("UPDATE notebooks SET sortOrder = :sortOrder WHERE id = :notebookId")
    suspend fun updateNotebookSortOrder(notebookId: Long, sortOrder: Int)

    @Query("UPDATE notebooks SET eventCount = (SELECT COUNT(*) FROM events WHERE notebookId = :notebookId AND isArchived = 0) WHERE id = :notebookId")
    suspend fun updateEventCount(notebookId: Long)

    @Query("SELECT COUNT(*) FROM notebooks")
    fun getNotebooksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM notebooks WHERE isDefault = 0")
    fun getCustomNotebooksCount(): Flow<Int>
}