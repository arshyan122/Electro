package com.example.electro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electro.adapter.RemoteServiceAdapter
import com.example.electro.databinding.FragmentBottomBinding
import com.example.electro.ui.common.UiState
import com.example.electro.ui.requests.BookServiceActivity
import com.example.electro.ui.search.SearchViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * "All Services" bottom sheet — observes SearchViewModel for the catalog.
 */
@AndroidEntryPoint
class BottomFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private val adapter = RemoteServiceAdapter { product ->
        startActivity(
            Intent(requireContext(), BookServiceActivity::class.java)
                .putExtra(BookServiceActivity.EXTRA_CATEGORY, product.category)
                .putExtra(BookServiceActivity.EXTRA_TITLE, product.title)
                .putExtra(BookServiceActivity.EXTRA_DESCRIPTION, product.description)
                .putExtra(BookServiceActivity.EXTRA_PRICE, product.price)
        )
        dismissAllowingStateLoss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomBinding.inflate(inflater, container, false)
        binding.menuRecylerView2.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecylerView2.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> Unit
                is UiState.Success -> adapter.submitList(state.data)
                is UiState.Error -> {
                    adapter.submitList(emptyList())
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.loadProducts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
