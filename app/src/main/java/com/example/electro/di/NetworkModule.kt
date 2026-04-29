package com.example.electro.di

import com.example.electro.BuildConfig
import com.example.electro.data.remote.ApiService
import com.example.electro.data.remote.AuthInterceptor
import com.example.electro.data.remote.GeminiApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Provides the network stack as application-scoped singletons.
 * `BuildConfig.BASE_URL` is set per build type in `app/build.gradle`.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    /**
     * Dedicated OkHttp client for Gemini. The app's [AuthInterceptor] adds the
     * Electro JWT to every request — Google's API rejects unrelated
     * Authorization headers with HTTP 400, so Gemini gets its own client
     * without that interceptor.
     */
    @Provides
    @Singleton
    @Named("gemini")
    fun provideGeminiOkHttpClient(
        logging: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    @Named("gemini")
    fun provideGeminiRetrofit(
        @Named("gemini") client: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideGeminiApi(@Named("gemini") retrofit: Retrofit): GeminiApi =
        retrofit.create(GeminiApi::class.java)
}
