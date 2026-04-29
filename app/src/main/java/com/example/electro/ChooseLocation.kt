package com.example.electro

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.electro.databinding.ActivityChooseLocationBinding
import com.example.electro.databinding.ActivityLoginBinding

@Suppress("DEPRECATION")
class ChooseLocation : AppCompatActivity() {
    private val binding: ActivityChooseLocationBinding by lazy {
        ActivityChooseLocationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val cities = resources.getStringArray(R.array.Cities)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cities)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        binding.nexttomain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


}