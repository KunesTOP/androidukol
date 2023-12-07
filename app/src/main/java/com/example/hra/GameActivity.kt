package com.example.hra

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hra.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {
    private lateinit var binding : ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val heroName = intent.getStringExtra("heroName")
        if (heroName != null) {
            // Set the heroName to the TextView
            binding.textHeroName.text = heroName
        }
    }
}