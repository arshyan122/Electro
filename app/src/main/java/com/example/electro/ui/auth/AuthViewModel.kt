package com.example.electro.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electro.data.model.User
import com.example.electro.data.repository.AuthRepository
import com.example.electro.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Backs LoginActivity and SignUpActivity. A single `authState` LiveData
 * surfaces Loading / Success<User> / Error so each activity renders the same
 * way.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableLiveData<UiState<User>>()
    val authState: LiveData<UiState<User>> = _authState

    fun login(email: String, password: String) {
        if (!validate(email, password)) return
        _authState.value = UiState.Loading
        viewModelScope.launch {
            _authState.value = repository.login(email.trim(), password).fold(
                onSuccess = { UiState.Success(it.user) },
                onFailure = { UiState.Error(it.localizedMessage ?: "Login failed.") }
            )
        }
    }

    fun signup(email: String, password: String, name: String) {
        if (!validate(email, password) || name.isBlank()) {
            _authState.value = UiState.Error("Name, email and password are required.")
            return
        }
        _authState.value = UiState.Loading
        viewModelScope.launch {
            _authState.value =
                repository.signup(email.trim(), password, name.trim()).fold(
                    onSuccess = { UiState.Success(it.user) },
                    onFailure = { UiState.Error(it.localizedMessage ?: "Signup failed.") }
                )
        }
    }

    fun isLoggedIn(): Boolean = repository.isLoggedIn()

    private fun validate(email: String, password: String): Boolean {
        if (email.isBlank() || !EMAIL_RE.matches(email.trim())) {
            _authState.value = UiState.Error("Enter a valid email.")
            return false
        }
        if (password.length < 6) {
            _authState.value = UiState.Error("Password must be at least 6 characters.")
            return false
        }
        return true
    }

    private companion object {
        val EMAIL_RE = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    }
}
