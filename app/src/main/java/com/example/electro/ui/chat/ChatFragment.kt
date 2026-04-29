package com.example.electro.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electro.R
import com.example.electro.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * AI-powered help screen. Replaces the old static HelpFragment. The
 * conversation lives in [ChatViewModel] so it survives rotation but resets
 * on process death.
 */
@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private val adapter = ChatAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        binding.recyclerView.adapter = adapter

        binding.sendButton.setOnClickListener { send() }
        binding.input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                send(); true
            } else false
        }
        binding.clearButton.setOnClickListener { viewModel.clear() }

        viewModel.messages.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list) {
                if (list.isNotEmpty()) {
                    binding.recyclerView.smoothScrollToPosition(list.size - 1)
                }
            }
        }

        viewModel.sending.observe(viewLifecycleOwner) { sending ->
            binding.progressBar.visibility = if (sending) View.VISIBLE else View.GONE
            binding.sendButton.isEnabled = !sending
            binding.sendButton.alpha = if (sending) 0.5f else 1f
        }

        binding.title.text = getString(R.string.chat_title)
    }

    private fun send() {
        val text = binding.input.text?.toString().orEmpty()
        if (text.isBlank()) return
        viewModel.send(text)
        binding.input.setText("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
