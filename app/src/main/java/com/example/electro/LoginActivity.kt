package com.example.electro

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.electro.databinding.ActivityLoginBinding
import com.example.electro.ui.auth.AuthViewModel
import com.example.electro.ui.common.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.loginbutton.setOnClickListener {
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()
            authViewModel.login(email, password)
        }
        binding.donthavebutton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
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
                Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
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
        binding.loginbutton.isEnabled = !loading
        binding.loginbutton.alpha = if (loading) 0.6f else 1f
    }
}
