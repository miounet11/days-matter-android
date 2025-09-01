package com.coquankedian.bigtime.ui

import androidx.lifecycle.*
import com.coquankedian.bigtime.data.model.Notebook
import com.coquankedian.bigtime.data.repository.AppRepository
import kotlinx.coroutines.launch

class NotebookViewModel(private val repository: AppRepository) : ViewModel() {

    val allNotebooks: LiveData<List<Notebook>> = repository.allNotebooks.asLiveData()
    val visibleNotebooks: LiveData<List<Notebook>> = repository.visibleNotebooks.asLiveData()
    val defaultNotebooks: LiveData<List<Notebook>> = repository.defaultNotebooks.asLiveData()
    val customNotebooks: LiveData<List<Notebook>> = repository.customNotebooks.asLiveData()
    val notebooksCount: LiveData<Int> = repository.notebooksCount.asLiveData()
    val customNotebooksCount: LiveData<Int> = repository.customNotebooksCount.asLiveData()

    fun insertNotebook(notebook: Notebook) = viewModelScope.launch {
        repository.insertNotebook(notebook)
    }

    fun updateNotebook(notebook: Notebook) = viewModelScope.launch {
        repository.updateNotebook(notebook)
    }

    fun deleteNotebook(notebook: Notebook) = viewModelScope.launch {
        repository.deleteNotebook(notebook)
    }

    fun deleteCustomNotebook(notebookId: Long) = viewModelScope.launch {
        repository.deleteCustomNotebook(notebookId)
    }

    fun updateNotebookVisibility(notebookId: Long, isHidden: Boolean) = viewModelScope.launch {
        repository.updateNotebookVisibility(notebookId, isHidden)
    }

    fun updateNotebookSortOrder(notebookId: Long, sortOrder: Int) = viewModelScope.launch {
        repository.updateNotebookSortOrder(notebookId, sortOrder)
    }

    fun updateEventCount(notebookId: Long) = viewModelScope.launch {
        repository.updateEventCount(notebookId)
    }

    suspend fun getNotebookById(notebookId: Long): Notebook? {
        return repository.getNotebookById(notebookId)
    }

    suspend fun getNotebookByName(name: String): Notebook? {
        return repository.getNotebookByName(name)
    }
}

class NotebookViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotebookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotebookViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}