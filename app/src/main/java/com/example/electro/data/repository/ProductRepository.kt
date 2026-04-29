package com.example.electro.data.repository

import com.example.electro.data.model.Product
import com.example.electro.data.remote.ApiService
import com.example.electro.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Single source of truth for product data. Repositories are intentionally
 * thin in this PR — they wrap network calls in `Result` and run them on the
 * IO dispatcher. Add caching (Room / in-memory) here when needed.
 */
class ProductRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    suspend fun getElectronics(): Result<List<Product>> = withContext(Dispatchers.IO) {
        runCatching { apiService.getElectronics() }
    }

    suspend fun getAllProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        runCatching { apiService.getAllProducts() }
    }
}
