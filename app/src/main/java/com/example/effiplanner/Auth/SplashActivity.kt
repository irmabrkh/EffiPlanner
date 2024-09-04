package com.example.effiplanner.Auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.effiplanner.MainActivity
import com.example.effiplanner.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(
            {
                val pindah = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(pindah)
                finish()
            },
            2000
        )
    }
}