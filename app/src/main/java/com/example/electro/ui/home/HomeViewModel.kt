package com.example.electro.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electro.data.model.Product
import com.example.electro.data.repository.ProductRepository
import com.example.electro.ui.common.UiState
import kotlinx.coroutines.launch

/**
 * Drives HomeFragment's "Popular Services" RecyclerView.
 *
 * Exposes a single `uiState` LiveData<UiState<List<Product>>> that the fragment
 * observes. `loadPopularProducts()` is idempotent and safe to call from
 * `onViewCreated`; it short-circuits if a successful load is already cached.
 */
class HomeViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState<List<Product>>>(UiState.Loading)
    val uiState: LiveData<UiState<List<Product>>> = _uiState

    fun loadPopularProducts(forceRefresh: Boolean = false) {
        val current = _uiState.value
        if (!forceRefresh && current is UiState.Success && current.data.isNotEmpty()) return

        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getElectronics()
            _uiState.value = result.fold(
                onSuccess = { products ->
                    if (products.isEmpty()) {
                        UiState.Error("No products available right now.")
                    } else {
                        UiState.Success(products)
                    }
                },
                onFailure = { throwable ->
                    UiState.Error(throwable.localizedMessage ?: "Unable to load products.")
                }
            )
        }
    }
}
