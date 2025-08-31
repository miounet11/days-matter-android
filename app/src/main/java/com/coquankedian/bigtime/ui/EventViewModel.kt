package com.coquankedian.bigtime.ui

import androidx.lifecycle.*
import com.coquankedian.bigtime.data.model.Event
import com.coquankedian.bigtime.data.repository.AppRepository
import kotlinx.coroutines.launch

class EventViewModel(private val repository: AppRepository) : ViewModel() {

    val allEvents: LiveData<List<Event>> = repository.allActiveEvents.asLiveData()
    val archivedEvents: LiveData<List<Event>> = repository.allArchivedEvents.asLiveData()

    fun getEventsByCategory(categoryId: Long): LiveData<List<Event>> =
        repository.getEventsByCategory(categoryId).asLiveData()

    fun searchEvents(query: String): LiveData<List<Event>> =
        repository.searchEvents(query).asLiveData()

    fun insertEvent(event: Event) = viewModelScope.launch {
        repository.insertEvent(event)
    }

    fun updateEvent(event: Event) = viewModelScope.launch {
        repository.updateEvent(event)
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
}

class EventViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
