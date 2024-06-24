package com.example.test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test.calculations.DivActivity
import com.example.test.calculations.MainActivity
import com.example.test.calculations.MulActivity
import com.example.test.calculations.SubActivity
import com.example.test.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up click listeners for card views or other UI elements
        binding.cardAddition.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.cardSubtraction.setOnClickListener {
            val intent = Intent(this, SubActivity::class.java)
            startActivity(intent)
        }

        binding.cardDivision.setOnClickListener {
            val intent = Intent(this, DivActivity::class.java)
            startActivity(intent)
        }

        binding.cardMultiplication.setOnClickListener {
            val intent = Intent(this, MulActivity::class.java)
            startActivity(intent)
        }

    }
}
