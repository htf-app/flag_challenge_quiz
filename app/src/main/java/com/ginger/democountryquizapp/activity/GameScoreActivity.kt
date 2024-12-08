package com.ginger.democountryquizapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ginger.democountryquizapp.R
import com.ginger.democountryquizapp.databinding.ActivityScoreCartBinding
import com.ginger.democountryquizapp.viewmodel.FirstViewModel

/**
 * @Author: mishrat khan
 * @Date: 07/12/24
 * @Time: 6:40 pm
 * @Project: DemoCountryQuizApp
 * @Package Name: com.ginger.democountryquizapp.activity
 */
class GameScoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScoreCartBinding
    private val viewModel: FirstViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("QuizState", MODE_PRIVATE)


        // Display "Game Over" initially
        binding.tvScoreCart.text = getString(R.string.game_over)

        // Retrieve the score passed via Intent
        val gameScore = intent.getIntExtra("GameScore", 0)

        // Use Handler to delay showing the score by 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            // After 2 seconds, show the score
            binding.tvScoreCart.text = getString(R.string.score_format, gameScore)

            // Optionally, you can add a message based on the score
            val scoreMessage = when {
                gameScore == 15 -> getString(R.string.perfect_score)
                gameScore > 10 -> getString(R.string.great_job)
                gameScore > 5 -> getString(R.string.good_try)
                else -> getString(R.string.better_luck_next_time)
            }

            binding.tvScoreCart.append("\n$scoreMessage")
        }, 1000) // 1000ms = 1 second delay


        // Handle back press with onBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Redirect to MainActivity and clear all previous activities
                sharedPreferences.edit().clear().apply()
                val intent = Intent(this@GameScoreActivity, FirstActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish() // End the current activity
            }
        })
    }

}
