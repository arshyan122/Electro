package com.example.electro.ui.common

/**
 * Uniform UI state envelope used by every ViewModel that drives a screen.
 * Fragments switch on this sealed class to render Loading / Success / Error.
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
