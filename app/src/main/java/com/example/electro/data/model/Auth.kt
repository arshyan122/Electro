package com.example.electro.data.model

import com.google.gson.annotations.SerializedName

data class User(
    /** Backend-assigned identifier. Mongo returns an ObjectId string here. */
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String
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
