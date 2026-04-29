package com.example.electro.ui.requests

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electro.databinding.ActivityMyRequestsBinding
import com.example.electro.ui.common.UiState
import dagger.hilt.android.AndroidEntryPoint

/**
 * Customer-facing list of service requests they've submitted, with cancel
 * support for pending/accepted requests.
 */
@AndroidEntryPoint
class MyRequestsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyRequestsBinding
    private val viewModel: MyRequestsViewModel by viewModels()

    private val adapter = RequestAdapter { req ->
        AlertDialog.Builder(this)
            .setTitle("Cancel request?")
            .setMessage("\"${req.title}\" will be cancelled. This cannot be undone.")
            .setPositiveButton("Cancel request") { _, _ ->
                viewModel.cancel(req.id) { ok, err ->
                    if (!ok) {
                        Toast.makeText(this, err ?: "Could not cancel", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Keep", null)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.emptyText.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(state.data)
                    binding.emptyText.visibility =
                        if (state.data.isEmpty()) View.VISIBLE else View.GONE
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Initial load is handled by onResume which always fires after
        // onCreate; calling it here too would fire a duplicate request.
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }
}
