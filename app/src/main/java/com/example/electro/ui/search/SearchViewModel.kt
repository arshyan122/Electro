package com.example.electro.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electro.data.model.Product
import com.example.electro.data.repository.ProductRepository
import com.example.electro.ui.common.UiState
import kotlinx.coroutines.launch

/**
 * Drives both the Search tab and the "All Services" bottom sheet.
 *
 * Loads the full electronics catalog once and exposes a derived,
 * client-side-filtered list via `setQuery(...)`.
 */
class SearchViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState<List<Product>>>(UiState.Loading)
    val uiState: LiveData<UiState<List<Product>>> = _uiState

    private var allProducts: List<Product> = emptyList()
    private var currentQuery: String = ""

    fun loadProducts(forceRefresh: Boolean = false) {
        if (!forceRefresh && allProducts.isNotEmpty()) {
            publishFiltered()
            return
        }
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            repository.getElectronics().fold(
                onSuccess = { products ->
                    allProducts = products
                    if (products.isEmpty()) {
                        _uiState.value = UiState.Error("No products available right now.")
                    } else {
                        publishFiltered()
                    }
                },
                onFailure = { throwable ->
                    _uiState.value = UiState.Error(
                        throwable.localizedMessage ?: "Unable to load products."
                    )
                }
            )
        }
    }

    fun setQuery(query: String?) {
        currentQuery = query.orEmpty()
        if (allProducts.isNotEmpty()) publishFiltered()
    }

    private fun publishFiltered() {
        val filtered = if (currentQuery.isBlank()) {
            allProducts
        } else {
            allProducts.filter { it.title.contains(currentQuery, ignoreCase = true) }
        }
        _uiState.value = UiState.Success(filtered)
    }
}
