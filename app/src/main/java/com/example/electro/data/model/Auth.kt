package com.example.electro.data.model

import com.google.gson.annotations.SerializedName

data class User(
    /** Backend-assigned identifier. Mongo returns an ObjectId string here. */
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String = "",
    @SerializedName("address") val address: String = "",
    @SerializedName("role") val role: String = "user"
)

/** Body for PATCH /auth/me. Any null field is omitted server-side. */
data class UpdateProfileRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("address") val address: String? = null
)

data class SignupRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("name") val name: String
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: User
)

/** Error envelope returned by the backend on 4xx/5xx. */
data class ApiError(
    @SerializedName("error") val error: String?
)
