package com.example.electro.data.repository

import com.example.electro.data.model.Service
import com.example.electro.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for the Electro service catalog (Wiring, AC, Fan, …).
 * Backed by `GET /services` on the Electro backend.
 */
@Singleton
class ServiceRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getServices(): Result<List<Service>> = withContext(Dispatchers.IO) {
        runCatching { apiService.getServices() }
    }

    suspend fun getServicesByCategory(category: String): Result<List<Service>> =
        withContext(Dispatchers.IO) {
            runCatching { apiService.getServicesByCategory(category) }
        }
}
