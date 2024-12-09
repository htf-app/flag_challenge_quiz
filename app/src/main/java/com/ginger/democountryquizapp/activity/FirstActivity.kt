package com.ginger.democountryquizapp.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ginger.democountryquizapp.databinding.ActivityFirstBinding
import com.ginger.democountryquizapp.viewmodel.FirstViewModel


/**
 * @Author: mishrat khan
 * @Date: 06/12/24
 * @Time: 5:00 pm
 * @Project: DemoCountryQuizApp
 * @Package Name: com.ginger.democountryquizapp.activity
 */


class FirstActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstBinding
    private val viewModel: FirstViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("QuizState", MODE_PRIVATE)

        // Restore state if it exists
        if (sharedPreferences.getInt("QuizStarted",0)==1) {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
            finish() // Optionally, finish this activity if you don't want the user to come back
        }


        binding.saveButton.setOnClickListener {
            val hours = binding.hourInput.text.toString().toIntOrNull() ?: 0
            val minutes = binding.minuteInput.text.toString().toIntOrNull() ?: 0
            val seconds = binding.secondInput.text.toString().toIntOrNull() ?: 0

            viewModel.saveTimer(hours, minutes, seconds)

            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("HOURS", hours)
            intent.putExtra("MINUTES", minutes)
            intent.putExtra("SECONDS", seconds)
            startActivity(intent)
        }
    }
}
