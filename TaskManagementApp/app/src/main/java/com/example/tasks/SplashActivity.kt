@file:Suppress("DEPRECATION")

package com.example.tasks


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.tasks.databinding.ActivitySplashBinding


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val SPLASH_DELAY: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide the action bar if it's not null
        Handler().postDelayed({
            // Start the main activity after the delay
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish() // Close the splash activity to prevent going back to it
        }, SPLASH_DELAY)
    }
}