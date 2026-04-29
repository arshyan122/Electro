package com.example.electro.data.remote

import com.example.electro.data.model.AuthResponse
import com.example.electro.data.model.LoginRequest
import com.example.electro.data.model.Product
import com.example.electro.data.model.Service
import com.example.electro.data.model.SignupRequest
import com.example.electro.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit interface for the Electro backend.
 *
 * Pointed at the Node + Express backend in `/backend` of this repo. See
 * `NetworkModule.BASE_URL` (driven by `BuildConfig.BASE_URL`) for the host.
 */
interface ApiService {

    // --- Auth ---

    @POST("auth/signup")
    suspend fun signup(@Body body: SignupRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @GET("auth/me")
    suspend fun me(): MeResponse

    // --- Products ---

    @GET("products")
    suspend fun getAllProducts(): List<Product>

    @GET("products/category/electronics")
    suspend fun getElectronics(): List<Product>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): List<Product>

    // --- Services ---

    @GET("services")
    suspend fun getServices(): List<Service>

    @GET("services/category/{category}")
    suspend fun getServicesByCategory(@Path("category") category: String): List<Service>
}

data class MeResponse(val user: User)
