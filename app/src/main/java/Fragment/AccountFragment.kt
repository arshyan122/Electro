package Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.electro.R
import com.example.electro.StartActivity
import com.example.electro.databinding.FragmentAccountBinding
import com.example.electro.ui.account.AccountViewModel
import com.example.electro.ui.common.UiState
import dagger.hilt.android.AndroidEntryPoint

/**
 * Account / profile editor. Reads the current user from `/auth/me`,
 * persists edits via `PATCH /auth/me`, and re-fetches on resume so two
 * devices stay in sync.
 */
@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefresh.setOnRefreshListener { viewModel.load() }
        binding.saveButton.setOnClickListener { onSaveClicked() }
        binding.logoutButton.setOnClickListener { onLogoutClicked() }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.swipeRefresh.isRefreshing = true
                    binding.saveButton.isEnabled = false
                }
                is UiState.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    binding.saveButton.isEnabled = true
                    populate(state.data.name, state.data.email, state.data.phone, state.data.address)
                }
                is UiState.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    binding.saveButton.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.events.observe(viewLifecycleOwner) { event ->
            when (event) {
                AccountViewModel.Event.Saving -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.saveButton.isEnabled = false
                }
                AccountViewModel.Event.SaveSuccess -> {
                    binding.progressBar.visibility = View.GONE
                    binding.saveButton.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.account_save_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.consumeEvent()
                }
                is AccountViewModel.Event.SaveFailure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.saveButton.isEnabled = true
                    Toast.makeText(requireContext(), event.message, Toast.LENGTH_LONG).show()
                    viewModel.consumeEvent()
                }
                null -> Unit
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }

    private fun populate(name: String, email: String, phone: String, address: String) {
        // Avoid trampling user-typed edits if they pulled to refresh mid-typing.
        if (binding.nameInput.text.isNullOrEmpty()) binding.nameInput.setText(name)
        if (binding.emailInput.text.isNullOrEmpty()) binding.emailInput.setText(email)
        if (binding.phoneInput.text.isNullOrEmpty()) binding.phoneInput.setText(phone)
        if (binding.addressInput.text.isNullOrEmpty()) binding.addressInput.setText(address)
    }

    private fun onSaveClicked() {
        val name = binding.nameInput.text?.toString().orEmpty().trim()
        val email = binding.emailInput.text?.toString().orEmpty().trim()
        val phone = binding.phoneInput.text?.toString().orEmpty().trim()
        val address = binding.addressInput.text?.toString().orEmpty().trim()

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), R.string.account_validation_name, Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), R.string.account_validation_email, Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.save(name, email, phone, address)
    }

    private fun onLogoutClicked() {
        viewModel.logout()
        startActivity(
            Intent(requireContext(), StartActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
