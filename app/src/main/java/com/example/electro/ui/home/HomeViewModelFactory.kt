package com.example.electro.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.electro.data.repository.ProductRepository

/**
 * Manual DI for HomeViewModel. Swap with Hilt/Koin/Dagger once the project
 * adopts a DI framework.
 */
class HomeViewModelFactory(
    private val repository: ProductRepository = ProductRepository()
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
