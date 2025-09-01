package com.coquankedian.bigtime.ui

import android.app.Application
import androidx.lifecycle.*
import com.coquankedian.bigtime.data.model.Event
import com.coquankedian.bigtime.data.repository.AppRepository
import com.coquankedian.bigtime.notification.ReminderManager
import kotlinx.coroutines.launch

class EventViewModel(
    private val repository: AppRepository,
    private val application: Application
) : ViewModel() {
    
    private val reminderManager = ReminderManager(application)

    // Current filter state
    private val _currentCategoryFilter = MutableLiveData<Long?>(null)
    val currentCategoryFilter: LiveData<Long?> = _currentCategoryFilter
    
    // Pinned events
    val pinnedEvents: LiveData<List<Event>> = repository.getPinnedEvents().asLiveData()
    
    // All active events (sorted with pinned at top)
    val allEvents: LiveData<List<Event>> = repository.allActiveEvents
        .asLiveData()
        .map { events ->
            events.sortedWith(compareBy(
                { !it.isPinned }, // Pinned events first
                { it.daysUntil }  // Then by days until
            ))
        }
    
    val archivedEvents: LiveData<List<Event>> = repository.allArchivedEvents.asLiveData()
    
    // Filtered events based on current category
    val filteredEvents: LiveData<List<Event>> = _currentCategoryFilter.switchMap { categoryId ->
        if (categoryId == null) {
            allEvents
        } else {
            repository.getEventsByCategory(categoryId)
                .asLiveData()
                .map { events ->
                    events.sortedWith(compareBy(
                        { !it.isPinned },
                        { it.daysUntil }
                    ))
                }
        }
    }
    
    fun setCategoryFilter(categoryId: Long?) {
        _currentCategoryFilter.value = categoryId
    }

    fun getEventsByCategory(categoryId: Long): LiveData<List<Event>> =
        repository.getEventsByCategory(categoryId).asLiveData()

    fun searchEvents(query: String): LiveData<List<Event>> =
        repository.searchEvents(query).asLiveData()

    fun insertEvent(event: Event) = viewModelScope.launch {
        val eventId = repository.insertEvent(event)
        
        // Schedule reminder if enabled
        if (event.reminderEnabled) {
            val savedEvent = event.copy(id = eventId)
            reminderManager.scheduleReminder(savedEvent)
        }
        
        // Create next repeat event if needed
        createNextRepeatEvent(event.copy(id = eventId))
    }

    fun updateEvent(event: Event) = viewModelScope.launch {
        repository.updateEvent(event)
        
        // Update reminder
        if (event.reminderEnabled) {
            reminderManager.scheduleReminder(event)
        } else {
            reminderManager.cancelReminder(event.id)
        }
    }

    fun deleteEvent(event: Event) = viewModelScope.launch {
        repository.deleteEvent(event)
    }

    fun archiveEvent(eventId: Long) = viewModelScope.launch {
        repository.archiveEvent(eventId)
    }

    fun unarchiveEvent(eventId: Long) = viewModelScope.launch {
        repository.unarchiveEvent(eventId)
    }

    fun updatePinnedStatus(eventId: Long, isPinned: Boolean) = viewModelScope.launch {
        repository.updatePinnedStatus(eventId, isPinned)
    }

    fun updateLockedStatus(eventId: Long, isLocked: Boolean) = viewModelScope.launch {
        repository.updateLockedStatus(eventId, isLocked)
    }

    suspend fun getEventById(eventId: Long): Event? {
        return repository.getEventById(eventId)
    }

    private suspend fun createNextRepeatEvent(event: Event) {
        if (event.repeatType == "NONE") return

        val calendar = java.util.Calendar.getInstance()
        calendar.time = event.date

        // Calculate next occurrence based on repeat type
        when (event.repeatType) {
            "DAILY" -> calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
            "WEEKLY" -> calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1)
            "MONTHLY" -> calendar.add(java.util.Calendar.MONTH, 1)
            "YEARLY" -> calendar.add(java.util.Calendar.YEAR, 1)
        }

        val nextDate = calendar.time
        val currentTime = java.util.Date()
        
        // Only create next occurrence if it's in the future
        if (nextDate.after(currentTime)) {
            val nextEvent = event.copy(
                id = 0, // New event
                date = nextDate,
                createdAt = currentTime,
                updatedAt = currentTime
            )
            
            val nextEventId = repository.insertEvent(nextEvent)
            
            // Schedule reminder for next event if enabled
            if (nextEvent.reminderEnabled) {
                val savedNextEvent = nextEvent.copy(id = nextEventId)
                reminderManager.scheduleReminder(savedNextEvent)
            }
        }
    }
}

class EventViewModelFactory(
    private val repository: AppRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
