package com.example.foodie.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.foodie.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Simulate a delay for loading, e.g., 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            // Start MainActivity after delay
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // Close SplashActivity so the user can't go back to it
            finish()
        }, 3000) // 3-second delay for the splash screen
    }
}
