@file:Suppress("DEPRECATION")

package com.example.todoproject


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.todoproject.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var logo: TextView
    private lateinit var display:ImageView
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