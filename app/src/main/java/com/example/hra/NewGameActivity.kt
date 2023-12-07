package com.example.hra

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.hra.databinding.ActivityNewGameBinding

class NewGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startBtn.setOnClickListener {
            if (binding.heroNameEdit.text.isEmpty()) {
                Toast.makeText(this, "Zadejte jméno hrdiny.", Toast.LENGTH_LONG).show()
            } else {
                try {
                    val i = Intent(this, GameActivity::class.java)
                    i.putExtra("heroName", binding.heroNameEdit.text.toString())
                    Log.d("NewGameActivity", "Starting GameActivity")
                    startActivity(i)
                } catch (e: Exception) {
                    Log.e("NewGameActivity", "Error starting GameActivity", e)
                }
            }
        }
    }
}