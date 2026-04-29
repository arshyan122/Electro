package com.example.electro.data.repository

import com.example.electro.data.local.TokenStorage
import com.example.electro.data.model.AuthResponse
import com.example.electro.data.model.LoginRequest
import com.example.electro.data.model.SignupRequest
import com.example.electro.data.model.User
import com.example.electro.data.remote.ApiService
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Owns auth state: signup, login, logout, current-user lookup. Persists the
 * JWT via `TokenStorage` so subsequent requests are auto-authenticated by
 * `AuthInterceptor`.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenStorage: TokenStorage
) {

    suspend fun signup(email: String, password: String, name: String): Result<AuthResponse> =
        withContext(Dispatchers.IO) {
            runCatching { apiService.signup(SignupRequest(email, password, name)) }
                .onSuccess { tokenStorage.saveToken(it.token) }
                .recoverCatching { throw it.toApiError() }
        }

    suspend fun login(email: String, password: String): Result<AuthResponse> =
        withContext(Dispatchers.IO) {
            runCatching { apiService.login(LoginRequest(email, password)) }
                .onSuccess { tokenStorage.saveToken(it.token) }
                .recoverCatching { throw it.toApiError() }
        }

    suspend fun fetchMe(): Result<User> = withContext(Dispatchers.IO) {
        runCatching { apiService.me().user }
            .recoverCatching { throw it.toApiError() }
    }

    fun logout() = tokenStorage.clear()
    fun isLoggedIn(): Boolean = tokenStorage.isLoggedIn()

    /** Best-effort extraction of the backend's `{"error":"..."}` body. */
    private fun Throwable.toApiError(): Throwable {
        if (this !is HttpException) return this
        val body = response()?.errorBody()?.string().orEmpty()
        if (body.isBlank()) return this
        return try {
            val parsed = Gson().fromJson(body, com.example.electro.data.model.ApiError::class.java)
            Throwable(parsed?.error ?: this.message ?: "Request failed.")
        } catch (e: JsonSyntaxException) {
            this
        }
    }
}
