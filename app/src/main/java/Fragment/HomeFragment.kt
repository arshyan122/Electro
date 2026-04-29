package Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.electro.BottomFragment
import com.example.electro.R
import com.example.electro.adapter.RemotePopularServiceAdapter
import com.example.electro.databinding.FragmentHomeBinding
import com.example.electro.ui.common.UiState
import com.example.electro.ui.home.HomeViewModel
import com.example.electro.ui.home.HomeViewModelFactory

/**
 * Home tab — image slider + remote-driven "Popular Services" list.
 *
 * The list is now backed by `HomeViewModel`/`ProductRepository` instead of
 * hard-coded data. UI/layout XML is intentionally untouched; only the data
 * source has changed.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels { HomeViewModelFactory() }
    private val popularAdapter = RemotePopularServiceAdapter { product ->
        Toast.makeText(requireContext(), product.title, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.allservicebutton.setOnClickListener {
            BottomFragment().show(parentFragmentManager, "AllServices")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupImageSlider()
        setupRecyclerView()
        observeUiState()
        viewModel.loadPopularProducts()
    }

    private fun setupImageSlider() {
        val imageList = arrayListOf(
            SlideModel(R.drawable.designer_2, ScaleTypes.FIT),
            SlideModel(R.drawable.designer3, ScaleTypes.FIT),
            SlideModel(R.drawable.designer_5, ScaleTypes.FIT),
            SlideModel(R.drawable.designer_6, ScaleTypes.FIT),
            SlideModel(R.drawable.designer_9, ScaleTypes.FIT),
            SlideModel(R.drawable.designer_10, ScaleTypes.FIT),
            SlideModel(R.drawable.designer_8, ScaleTypes.FIT)
        )
        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
        binding.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                Toast.makeText(
                    requireContext(),
                    "Selected Image $position",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun doubleClick(position: Int) = Unit
        })
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = popularAdapter
    }

    private fun observeUiState() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.loadingProgressBar.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    binding.loadingProgressBar.visibility = View.GONE
                    popularAdapter.submitList(state.data)
                }
                is UiState.Error -> {
                    binding.loadingProgressBar.visibility = View.GONE
                    popularAdapter.submitList(emptyList())
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
