package com.example.electro

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.electro.databinding.ActivitySignUpBinding
import com.example.electro.ui.auth.AuthViewModel
import com.example.electro.ui.common.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.alreadyaccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding.button6.setOnClickListener {
            val name = binding.editTextTextPersonName.text.toString()
            val email = binding.editTextTextEmailAddress2.text.toString()
            val password = binding.editTextTextPassword2.text.toString()
            authViewModel.signup(email, password, name)
        }

        authViewModel.authState.observe(this) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: UiState<*>) {
        when (state) {
            is UiState.Loading -> setLoading(true)
            is UiState.Success<*> -> {
                setLoading(false)
                Toast.makeText(this, "Account created.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ChooseLocation::class.java))
                finish()
            }
            is UiState.Error -> {
                setLoading(false)
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.button6.isEnabled = !loading
        binding.button6.alpha = if (loading) 0.6f else 1f
    }
}
