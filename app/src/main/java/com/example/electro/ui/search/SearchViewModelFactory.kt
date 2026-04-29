package com.example.electro.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.electro.data.repository.ProductRepository

/**
 * Manual DI for SearchViewModel. Mirrors HomeViewModelFactory; both go away
 * once Hilt lands.
 */
class SearchViewModelFactory(
    private val repository: ProductRepository = ProductRepository()
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
