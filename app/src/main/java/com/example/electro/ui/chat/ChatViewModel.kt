package com.example.electro.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electro.data.repository.GeminiRepository
import com.example.electro.data.repository.GeminiRepository.ChatRole
import com.example.electro.data.repository.GeminiRepository.ChatTurn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds an in-memory chat history and a "sending" flag for the chat
 * screen. The history survives configuration changes (rotation) because
 * the ViewModel does, but is intentionally NOT persisted across process
 * death.
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: GeminiRepository
) : ViewModel() {

    private val _messages = MutableLiveData<List<ChatMessage>>(initialMessages())
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _sending = MutableLiveData(false)
    val sending: LiveData<Boolean> = _sending

    fun send(rawText: String) {
        val text = rawText.trim()
        if (text.isEmpty() || _sending.value == true) return

        // Optimistically add the user's bubble.
        val withUser = (_messages.value.orEmpty()) + ChatMessage(
            role = ChatRole.USER,
            text = text
        )
        _messages.value = withUser
        _sending.value = true

        viewModelScope.launch {
            try {
                val history = withUser
                    .filter { !it.isError }
                    .map { ChatTurn(it.role, it.text) }
                val reply = repository.reply(history)
                _messages.value = (_messages.value.orEmpty()) + ChatMessage(
                    role = ChatRole.ASSISTANT,
                    text = reply
                )
            } catch (t: Throwable) {
                _messages.value = (_messages.value.orEmpty()) + ChatMessage(
                    role = ChatRole.ASSISTANT,
                    text = friendlyError(t),
                    isError = true
                )
            } finally {
                _sending.value = false
            }
        }
    }

    fun clear() {
        _messages.value = initialMessages()
    }

    private fun initialMessages(): List<ChatMessage> = listOf(
        ChatMessage(
            role = ChatRole.ASSISTANT,
            text = "Hi! I'm the Electro assistant. Ask me anything about " +
                "booking services, your account, or how the app works."
        )
    )

    private fun friendlyError(t: Throwable): String {
        val msg = t.message.orEmpty()
        return when {
            msg.contains("API key", ignoreCase = true) ->
                "Sorry, the assistant isn't configured yet. Please contact the developer."
            msg.contains("Unable to resolve host", ignoreCase = true) ||
                msg.contains("timeout", ignoreCase = true) ->
                "Couldn't reach the assistant — please check your internet and try again."
            else -> "Sorry, something went wrong: $msg"
        }
    }
}

/** UI-layer chat row. Distinct from the network DTO. */
data class ChatMessage(
    val role: ChatRole,
    val text: String,
    val isError: Boolean = false
)
