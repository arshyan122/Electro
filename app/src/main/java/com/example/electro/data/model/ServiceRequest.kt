package com.example.electro.data.model

import com.google.gson.annotations.SerializedName

/**
 * Service request as stored on the backend (`/requests` collection).
 *
 * Mirrors the JSON shape from `backend/src/models/Request.js` after `toJSON`
 * transformation. `customerId` and `technicianId` are ObjectId hex strings.
 */
data class ServiceRequest(
    @SerializedName("id") val id: String,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("technicianId") val technicianId: String? = null,
    @SerializedName("category") val category: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String = "",
    @SerializedName("address") val address: String = "",
    @SerializedName("price") val price: Double = 0.0,
    @SerializedName("status") val status: String,
    @SerializedName("rejectedBy") val rejectedBy: List<String> = emptyList(),
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

/** Body for `POST /requests` (the customer creating a new request). */
data class CreateRequestBody(
    @SerializedName("category") val category: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String = "",
    @SerializedName("address") val address: String = "",
    @SerializedName("price") val price: Double = 0.0
)
