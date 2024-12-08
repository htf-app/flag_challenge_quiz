package com.ginger.democountryquizapp.activity

import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
