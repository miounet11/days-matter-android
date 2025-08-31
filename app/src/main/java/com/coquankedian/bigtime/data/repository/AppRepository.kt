package com.coquankedian.bigtime.data.repository

import com.coquankedian.bigtime.data.dao.CategoryDao
import com.coquankedian.bigtime.data.dao.EventDao
import com.coquankedian.bigtime.data.model.Category
import com.coquankedian.bigtime.data.model.Event
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val eventDao: EventDao,
    private val categoryDao: CategoryDao
) {

    // Event operations
    val allActiveEvents: Flow<List<Event>> = eventDao.getAllActiveEvents()
    val allArchivedEvents: Flow<List<Event>> = eventDao.getAllArchivedEvents()
    val activeEventsCount: Flow<Int> = eventDao.getActiveEventsCount()

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
}
