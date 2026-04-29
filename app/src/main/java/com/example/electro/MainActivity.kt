package com.example.electro
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.electro.ui.requests.MyRequestsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val avController = findNavController(R.id.fragmentContainerView)
        val bottomnav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomnav.setupWithNavController(avController)

        // Tap the welcome banner to view your service-request history. The
        // banner is the only static text on every tab, so it doubles as the
        // entry point without modifying any existing layouts.
        findViewById<TextView>(R.id.textView16)?.setOnClickListener {
            startActivity(Intent(this, MyRequestsActivity::class.java))
        }
    }
}
