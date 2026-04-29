package com.example.electro
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.electro.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var avController =findNavController(R.id.fragmentContainerView)
        var bottomnav =findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomnav.setupWithNavController(avController)
    }
}
