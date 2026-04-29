package com.example.electro.data.model

import com.google.gson.annotations.SerializedName

/**
 * Domain/transport model for an electronics item returned by the products API.
 * Mapped from https://fakestoreapi.com/products/category/electronics — swap
 * `BASE_URL` and field names if you wire a different backend.
 */
data class Product(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: Double,
    @SerializedName("description") val description: String,
    @SerializedName("category") val category: String,
    @SerializedName("image") val imageUrl: String,
    @SerializedName("rating") val rating: Rating? = null
) {
    data class Rating(
        @SerializedName("rate") val rate: Double,
        @SerializedName("count") val count: Int
    )
}
