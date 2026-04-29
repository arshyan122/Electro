package com.example.electro.ui.requests

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.electro.data.model.CreateRequestBody
import com.example.electro.databinding.ActivityBookServiceBinding
import com.example.electro.ui.common.UiState
import dagger.hilt.android.AndroidEntryPoint

/**
 * Customer-facing form that creates a `ServiceRequest` via `POST /requests`.
 *
 * Reads optional `extra_*` extras to prefill from a tapped service item:
 *  - EXTRA_CATEGORY: prefills the Category field
 *  - EXTRA_TITLE: prefills the Title field
 *  - EXTRA_DESCRIPTION: prefills the Description field
 *  - EXTRA_PRICE: prefills the Budget field (Double, > 0)
 *
 * On success, navigates to [MyRequestsActivity].
 */
@AndroidEntryPoint
class BookServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookServiceBinding
    private val viewModel: BookServiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefillFromExtras()
        observeUiState()

        binding.submitButton.setOnClickListener { submit() }
    }

    private fun prefillFromExtras() {
        intent?.getStringExtra(EXTRA_CATEGORY)?.let {
            binding.categoryInput.setText(it)
        }
        intent?.getStringExtra(EXTRA_TITLE)?.let {
            binding.titleInput.setText(it)
        }
        intent?.getStringExtra(EXTRA_DESCRIPTION)?.let {
            binding.descriptionInput.setText(it)
        }
        val price = intent?.getDoubleExtra(EXTRA_PRICE, 0.0) ?: 0.0
        if (price > 0.0) {
            binding.priceInput.setText(price.toString())
        }
    }

    private fun submit() {
        val category = binding.categoryInput.text?.toString()?.trim().orEmpty()
        val title = binding.titleInput.text?.toString()?.trim().orEmpty()
        val description = binding.descriptionInput.text?.toString()?.trim().orEmpty()
        val address = binding.addressInput.text?.toString()?.trim().orEmpty()
        val price = binding.priceInput.text?.toString()?.trim().orEmpty()
            .toDoubleOrNull() ?: 0.0

        if (category.isEmpty()) {
            binding.categoryInput.error = "Required"
            return
        }
        if (title.isEmpty()) {
            binding.titleInput.error = "Required"
            return
        }
        if (address.isEmpty()) {
            binding.addressInput.error = "Required"
            return
        }
        viewModel.submit(
            CreateRequestBody(
                category = category,
                title = title,
                description = description,
                address = address,
                price = price
            )
        )
    }

    private fun observeUiState() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.submitButton.isEnabled = false
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.submitButton.isEnabled = true
                    Toast.makeText(
                        this,
                        "Request created. A technician will be notified.",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this, MyRequestsActivity::class.java))
                    finish()
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.submitButton.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_PRICE = "extra_price"
    }
}
