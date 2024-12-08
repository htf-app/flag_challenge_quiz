package com.ginger.democountryquizapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FirstViewModel : ViewModel() {

    private val _timerData = MutableLiveData<Triple<Int, Int, Int>>()
    val timerData: LiveData<Triple<Int, Int, Int>> get() = _timerData

    fun saveTimer(hours: Int, minutes: Int, seconds: Int) {
        _timerData.value = Triple(hours, minutes, seconds)
    }
}
