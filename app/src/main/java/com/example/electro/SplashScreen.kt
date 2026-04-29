package com.example.electro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.electro.data.local.TokenStorage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Decides where the user goes when the app launches:
 *   - signed in (token present)  → straight to [MainActivity]
 *   - otherwise                  → [StartActivity], which leads to login
 *
 * The 3-second delay is preserved so the splash animation has time to play.
 */
@Suppress("DEPRECATION")
@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {

    @Inject lateinit var tokenStorage: TokenStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val target = if (tokenStorage.isLoggedIn()) {
                MainActivity::class.java
            } else {
                StartActivity::class.java
            }
            startActivity(Intent(this, target))
            finish()
        }, 3000)
    }
}
