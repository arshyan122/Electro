package com.example.electro.data.repository

import com.example.electro.data.model.CreateRequestBody
import com.example.electro.data.model.ServiceRequest
import com.example.electro.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Customer-side wrapper around the `/requests` endpoints.
 *
 * Throws on transport / API errors; callers should wrap calls in try/catch and
 * surface failures to the UI as `UiState.Error`.
 */
@Singleton
class RequestRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun create(body: CreateRequestBody): ServiceRequest = api.createRequest(body)

    suspend fun listMine(): List<ServiceRequest> = api.getMyRequests()

    suspend fun cancel(id: String): ServiceRequest = api.cancelRequest(id)
}
