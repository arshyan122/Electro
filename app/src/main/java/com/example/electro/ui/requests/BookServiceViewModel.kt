package com.example.electro.ui.requests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electro.data.model.CreateRequestBody
import com.example.electro.data.model.ServiceRequest
import com.example.electro.data.repository.RequestRepository
import com.example.electro.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Drives the Book Service form. Exposes a single `uiState` LiveData that
 * emits Loading → Success(ServiceRequest) | Error on submit.
 */
@HiltViewModel
class BookServiceViewModel @Inject constructor(
    private val repository: RequestRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState<ServiceRequest>>()
    val uiState: LiveData<UiState<ServiceRequest>> = _uiState

    fun submit(body: CreateRequestBody) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val created = repository.create(body)
                _uiState.value = UiState.Success(created)
            } catch (t: Throwable) {
                _uiState.value = UiState.Error(t.message ?: "Could not create request.")
            }
        }
    }
}
