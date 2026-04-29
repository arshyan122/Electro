package com.example.electro.data.remote

import com.example.electro.data.model.Product
import retrofit2.http.GET

/**
 * Retrofit interface for the Electro backend.
 *
 * Currently points at the public Fake Store API which returns electronics-shaped
 * JSON. Replace these endpoints with the real Electro backend when available.
 */
interface ApiService {

    @GET("products/category/electronics")
    suspend fun getElectronics(): List<Product>

    @GET("products")
    suspend fun getAllProducts(): List<Product>
}
