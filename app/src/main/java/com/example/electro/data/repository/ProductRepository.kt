package com.example.electro.data.repository

import com.example.electro.data.model.Product
import com.example.electro.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for product data. Repositories are intentionally
 * thin in this PR — they wrap network calls in `Result` and run them on the
 * IO dispatcher. Add caching (Room / in-memory) here when needed.
 */
@Singleton
class ProductRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getElectronics(): Result<List<Product>> = withContext(Dispatchers.IO) {
        runCatching { apiService.getElectronics() }
    }

    suspend fun getAllProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        runCatching { apiService.getAllProducts() }
    }
}
