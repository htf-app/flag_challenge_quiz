package com.ginger.democountryquizapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.os.CountDownTimer

class SecondViewModel : ViewModel() {

    private val _timeLeft = MutableLiveData<String>()
    val timeLeft: LiveData<String> get() = _timeLeft

    private val _timerFinished = MutableLiveData<Boolean>()
    val timerFinished: LiveData<Boolean> get() = _timerFinished

    private lateinit var countDownTimer: CountDownTimer

    fun startTimer(totalMillis: Long) {
        countDownTimer = object : CountDownTimer(totalMillis, 1000) { // Update every second
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000) % 60
                val minutes = (millisUntilFinished / 1000) / 60
                val formattedTime = String.format("%02d:%02d", minutes, seconds)
                _timeLeft.postValue(formattedTime)
            }

            override fun onFinish() {
                _timerFinished.postValue(true)
            }
        }

        countDownTimer.start()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer.cancel() // Clean up timer when ViewModel is destroyed
    }
}
