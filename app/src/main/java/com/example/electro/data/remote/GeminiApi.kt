package com.example.electro.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for Google Gemini's `generateContent` REST endpoint.
 *
 * The base URL `https://generativelanguage.googleapis.com/` is configured
 * separately from the Electro backend in [com.example.electro.di.NetworkModule].
 *
 * API reference:
 *   https://ai.google.dev/api/generate-content
 */
interface GeminiApi {

    /**
     * One-shot generation. The full conversation is passed as `contents`;
     * Gemini is stateless so the client must replay the history with each
     * call.
     */
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generate(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body body: GeminiRequest
    ): GeminiResponse
}

// --- Request ---

data class GeminiRequest(
    @SerializedName("contents") val contents: List<GeminiContent>,
    @SerializedName("systemInstruction") val systemInstruction: GeminiContent? = null,
    @SerializedName("generationConfig") val generationConfig: GeminiGenerationConfig? = null
)

data class GeminiContent(
    /** "user" or "model" — Gemini does not accept "assistant". */
    @SerializedName("role") val role: String,
    @SerializedName("parts") val parts: List<GeminiPart>
)

data class GeminiPart(
    @SerializedName("text") val text: String
)

data class GeminiGenerationConfig(
    @SerializedName("temperature") val temperature: Double = 0.7,
    @SerializedName("maxOutputTokens") val maxOutputTokens: Int = 1024
)

// --- Response ---

data class GeminiResponse(
    @SerializedName("candidates") val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    @SerializedName("content") val content: GeminiContent?,
    @SerializedName("finishReason") val finishReason: String?
)
