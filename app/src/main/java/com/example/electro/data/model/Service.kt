package com.example.electro.data.model

import com.google.gson.annotations.SerializedName

/**
 * A service offered by Electro (e.g. Wiring, AC repair, Fan service).
 * Mapped from `GET /services` on the Electro backend.
 */
data class Service(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("icon") val iconUrl: String,
    @SerializedName("category") val category: String
)
