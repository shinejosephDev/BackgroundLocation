package com.sample.backgroundlocation.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.sample.backgroundlocation.background.LocationService
import com.sample.backgroundlocation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        binding.buttonTracking.setOnClickListener {
            val intent = Intent(this, LocationService::class.java)
            ContextCompat.startForegroundService(this,intent)
        }
    }
}