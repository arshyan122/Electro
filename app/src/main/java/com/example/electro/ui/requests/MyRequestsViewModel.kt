package com.example.electro.ui.requests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electro.data.model.ServiceRequest
import com.example.electro.data.repository.RequestRepository
import com.example.electro.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Backs the "My Requests" screen. Loads the customer's request history and
 * supports cancelling pending/accepted requests.
 */
@HiltViewModel
class MyRequestsViewModel @Inject constructor(
    private val repository: RequestRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState<List<ServiceRequest>>>()
    val uiState: LiveData<UiState<List<ServiceRequest>>> = _uiState

    fun load() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Success(repository.listMine())
            } catch (t: Throwable) {
                _uiState.value = UiState.Error(t.message ?: "Could not load requests.")
            }
        }
    }

    /** Optimistically updates the in-memory list when a cancel succeeds. */
    fun cancel(id: String, onResult: (ok: Boolean, message: String?) -> Unit) {
        viewModelScope.launch {
            try {
                val updated = repository.cancel(id)
                val current = (_uiState.value as? UiState.Success)?.data.orEmpty()
                _uiState.value = UiState.Success(
                    current.map { if (it.id == updated.id) updated else it }
                )
                onResult(true, null)
            } catch (t: Throwable) {
                onResult(false, t.message)
            }
        }
    }
}
