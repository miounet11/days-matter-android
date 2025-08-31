package com.coquankedian.bigtime.data.dao

import androidx.room.*
import com.coquankedian.bigtime.data.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events WHERE isArchived = 0 ORDER BY isPinned DESC, date ASC")
    fun getAllActiveEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE isArchived = 1 ORDER BY updatedAt DESC")
    fun getAllArchivedEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE categoryId = :categoryId AND isArchived = 0 ORDER BY isPinned DESC, date ASC")
    fun getEventsByCategory(categoryId: Long): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: Long): Event?

    @Query("SELECT * FROM events WHERE date >= :startDate AND date <= :endDate AND isArchived = 0")
    fun getEventsInDateRange(startDate: Long, endDate: Long): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE isPinned = 1 AND isArchived = 0 ORDER BY date ASC")
    fun getPinnedEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE isLocked = 1 AND isArchived = 0")
    fun getLockedEvents(): Flow<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event): Long

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteEventById(eventId: Long)

    @Query("UPDATE events SET isArchived = 1, updatedAt = :currentTime WHERE id = :eventId")
    suspend fun archiveEvent(eventId: Long, currentTime: Long = System.currentTimeMillis())

    @Query("UPDATE events SET isArchived = 0, updatedAt = :currentTime WHERE id = :eventId")
    suspend fun unarchiveEvent(eventId: Long, currentTime: Long = System.currentTimeMillis())

    @Query("UPDATE events SET isPinned = :isPinned, updatedAt = :currentTime WHERE id = :eventId")
    suspend fun updatePinnedStatus(eventId: Long, isPinned: Boolean, currentTime: Long = System.currentTimeMillis())

    @Query("UPDATE events SET isLocked = :isLocked, updatedAt = :currentTime WHERE id = :eventId")
    suspend fun updateLockedStatus(eventId: Long, isLocked: Boolean, currentTime: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM events WHERE isArchived = 0")
    fun getActiveEventsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM events WHERE categoryId = :categoryId AND isArchived = 0")
    fun getEventsCountByCategory(categoryId: Long): Flow<Int>

    // Search functionality
    @Query("SELECT * FROM events WHERE (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') AND isArchived = 0 ORDER BY isPinned DESC, date ASC")
    fun searchEvents(query: String): Flow<List<Event>>
}
