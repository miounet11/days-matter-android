package com.coquankedian.bigtime.data.repository

import com.coquankedian.bigtime.data.dao.CategoryDao
import com.coquankedian.bigtime.data.dao.EventDao
import com.coquankedian.bigtime.data.dao.NotebookDao
import com.coquankedian.bigtime.data.model.Category
import com.coquankedian.bigtime.data.model.Event
import com.coquankedian.bigtime.data.model.Notebook
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val eventDao: EventDao,
    private val categoryDao: CategoryDao,
    private val notebookDao: NotebookDao
) {

    // Event operations
    val allActiveEvents: Flow<List<Event>> = eventDao.getAllActiveEvents()
    val allArchivedEvents: Flow<List<Event>> = eventDao.getAllArchivedEvents()
    val activeEventsCount: Flow<Int> = eventDao.getActiveEventsCount()
    
    fun getPinnedEvents(): Flow<List<Event>> = eventDao.getPinnedEvents()

    fun getEventsByCategory(categoryId: Long): Flow<List<Event>> =
        eventDao.getEventsByCategory(categoryId)

    fun getEventsCountByCategory(categoryId: Long): Flow<Int> =
        eventDao.getEventsCountByCategory(categoryId)

    fun searchEvents(query: String): Flow<List<Event>> =
        eventDao.searchEvents(query)

    suspend fun getEventById(eventId: Long): Event? =
        eventDao.getEventById(eventId)

    suspend fun insertEvent(event: Event): Long =
        eventDao.insertEvent(event)

    suspend fun updateEvent(event: Event) =
        eventDao.updateEvent(event)

    suspend fun deleteEvent(event: Event) =
        eventDao.deleteEvent(event)

    suspend fun archiveEvent(eventId: Long) =
        eventDao.archiveEvent(eventId)

    suspend fun unarchiveEvent(eventId: Long) =
        eventDao.unarchiveEvent(eventId)

    suspend fun updatePinnedStatus(eventId: Long, isPinned: Boolean) =
        eventDao.updatePinnedStatus(eventId, isPinned)

    suspend fun updateLockedStatus(eventId: Long, isLocked: Boolean) =
        eventDao.updateLockedStatus(eventId, isLocked)

    // Category operations
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()
    val defaultCategories: Flow<List<Category>> = categoryDao.getDefaultCategories()
    val customCategories: Flow<List<Category>> = categoryDao.getCustomCategories()

    suspend fun getCategoryById(categoryId: Long): Category? =
        categoryDao.getCategoryById(categoryId)

    suspend fun insertCategory(category: Category): Long =
        categoryDao.insertCategory(category)

    suspend fun updateCategory(category: Category) =
        categoryDao.updateCategory(category)

    suspend fun deleteCustomCategory(categoryId: Long): Int =
        categoryDao.deleteCustomCategory(categoryId)

    // Notebook operations
    val allNotebooks: Flow<List<Notebook>> = notebookDao.getAllNotebooks()
    val visibleNotebooks: Flow<List<Notebook>> = notebookDao.getVisibleNotebooks()
    val defaultNotebooks: Flow<List<Notebook>> = notebookDao.getDefaultNotebooks()
    val customNotebooks: Flow<List<Notebook>> = notebookDao.getCustomNotebooks()
    val notebooksCount: Flow<Int> = notebookDao.getNotebooksCount()
    val customNotebooksCount: Flow<Int> = notebookDao.getCustomNotebooksCount()

    suspend fun getNotebookById(notebookId: Long): Notebook? =
        notebookDao.getNotebookById(notebookId)

    suspend fun getNotebookByName(name: String): Notebook? =
        notebookDao.getNotebookByName(name)

    suspend fun insertNotebook(notebook: Notebook): Long =
        notebookDao.insertNotebook(notebook)

    suspend fun updateNotebook(notebook: Notebook) =
        notebookDao.updateNotebook(notebook)

    suspend fun deleteNotebook(notebook: Notebook) =
        notebookDao.deleteNotebook(notebook)

    suspend fun deleteCustomNotebook(notebookId: Long): Int =
        notebookDao.deleteCustomNotebook(notebookId)

    suspend fun updateNotebookVisibility(notebookId: Long, isHidden: Boolean) =
        notebookDao.updateNotebookVisibility(notebookId, isHidden)

    suspend fun updateNotebookSortOrder(notebookId: Long, sortOrder: Int) =
        notebookDao.updateNotebookSortOrder(notebookId, sortOrder)

    suspend fun updateEventCount(notebookId: Long) =
        notebookDao.updateEventCount(notebookId)
}
