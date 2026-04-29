package com.example.electro.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electro.R
import com.example.electro.data.repository.GeminiRepository.ChatRole
import com.example.electro.databinding.ItemChatAssistantBinding
import com.example.electro.databinding.ItemChatUserBinding

/**
 * Two-view-type list of chat messages. Assistant bubbles are aligned start,
 * user bubbles end. Errors get a tinted bubble.
 */
class ChatAdapter : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(DIFF) {

    override fun getItemViewType(position: Int): Int =
        if (getItem(position).role == ChatRole.USER) TYPE_USER else TYPE_ASSISTANT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_USER) {
            UserVH(ItemChatUserBinding.inflate(inflater, parent, false))
        } else {
            AssistantVH(ItemChatAssistantBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = getItem(position)
        when (holder) {
            is UserVH -> holder.bind(msg)
            is AssistantVH -> holder.bind(msg)
        }
    }

    class UserVH(private val binding: ItemChatUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: ChatMessage) {
            binding.bubbleText.text = msg.text
        }
    }

    class AssistantVH(private val binding: ItemChatAssistantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: ChatMessage) {
            binding.bubbleText.text = msg.text
            binding.bubble.setBackgroundResource(
                if (msg.isError) R.drawable.bg_chat_bubble_error
                else R.drawable.bg_chat_bubble_assistant
            )
        }
    }

    companion object {
        private const val TYPE_USER = 1
        private const val TYPE_ASSISTANT = 2

        private val DIFF = object : DiffUtil.ItemCallback<ChatMessage>() {
            override fun areItemsTheSame(old: ChatMessage, new: ChatMessage): Boolean =
                old === new
            override fun areContentsTheSame(old: ChatMessage, new: ChatMessage): Boolean =
                old == new
        }
    }
}
