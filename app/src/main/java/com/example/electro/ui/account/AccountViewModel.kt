package com.example.electro.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electro.data.model.UpdateProfileRequest
import com.example.electro.data.model.User
import com.example.electro.data.repository.AuthRepository
import com.example.electro.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Drives the Account screen: loads the current user, persists edits via
 * PATCH /auth/me. The fragment owns ephemeral form state; this ViewModel
 * owns the network state.
 */
@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableLiveData<UiState<User>>(UiState.Loading)
    val state: LiveData<UiState<User>> = _state

    /** One-shot save events. Cleared after the fragment consumes them. */
    private val _events = MutableLiveData<Event?>(null)
    val events: LiveData<Event?> = _events

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            repository.fetchMe().fold(
                onSuccess = { _state.value = UiState.Success(it) },
                onFailure = { _state.value = UiState.Error(it.message ?: "Couldn't load profile.") }
            )
        }
    }

    fun save(name: String, email: String, phone: String, address: String) {
        _events.value = Event.Saving
        viewModelScope.launch {
            repository.updateMe(
                UpdateProfileRequest(
                    name = name,
                    email = email,
                    phone = phone,
                    address = address
                )
            ).fold(
                onSuccess = {
                    _state.value = UiState.Success(it)
                    _events.value = Event.SaveSuccess
                },
                onFailure = {
                    _events.value = Event.SaveFailure(it.message ?: "Couldn't save.")
                }
            )
        }
    }

    fun consumeEvent() { _events.value = null }

    fun logout() {
        repository.logout()
    }

    sealed class Event {
        object Saving : Event()
        object SaveSuccess : Event()
        data class SaveFailure(val message: String) : Event()
    }
}
