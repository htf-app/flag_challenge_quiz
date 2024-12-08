package com.ginger.democountryquizapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ginger.democountryquizapp.databinding.ActivitySecondBinding
import com.ginger.democountryquizapp.viewmodel.SecondViewModel


/**
 * @Author: mishrat khan
 * @Date: 06/12/24
 * @Time: 5:00 pm
 * @Project: DemoCountryQuizApp
 * @Package Name: com.ginger.democountryquizapp.activity
 */

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding
    private val viewModel: SecondViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the timer duration passed from FirstActivity
        val hours = intent.getIntExtra("HOURS", 0)
        val minutes = intent.getIntExtra("MINUTES", 0)
        val seconds = intent.getIntExtra("SECONDS", 0)
        val totalMillis = (hours * 3600 + minutes * 60 + seconds) * 1000L

        // Start the countdown timer
        viewModel.startTimer(totalMillis)

        // Observe the remaining time
        viewModel.timeLeft.observe(this, Observer { time ->
            binding.timerText.text = time
            binding.timerText1.text = time

        })

        // Observe if the timer is finished and navigate to QuizActivity
        viewModel.timerFinished.observe(this, Observer {
            if (it) {
                navigateToQuizActivity()
            }
        })
    }

    private fun navigateToQuizActivity() {
        val intent = Intent(this, QuizActivity::class.java)
        startActivity(intent)
        finish() // Optionally, finish this activity if you don't want the user to come back
    }
}
