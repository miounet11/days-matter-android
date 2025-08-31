package com.coquankedian.bigtime.ui

import androidx.lifecycle.*
import com.coquankedian.bigtime.data.model.Category
import com.coquankedian.bigtime.data.repository.AppRepository
import kotlinx.coroutines.launch

class CategoryViewModel(private val repository: AppRepository) : ViewModel() {

    val allCategories: LiveData<List<Category>> = repository.allCategories.asLiveData()
    val defaultCategories: LiveData<List<Category>> = repository.defaultCategories.asLiveData()
    val customCategories: LiveData<List<Category>> = repository.customCategories.asLiveData()

    fun insertCategory(category: Category) = viewModelScope.launch {
        repository.insertCategory(category)
    }

    fun updateCategory(category: Category) = viewModelScope.launch {
        repository.updateCategory(category)
    }

    fun deleteCustomCategory(categoryId: Long) = viewModelScope.launch {
        repository.deleteCustomCategory(categoryId)
    }

    suspend fun getCategoryById(categoryId: Long): Category? {
        return repository.getCategoryById(categoryId)
    }
}

class CategoryViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
