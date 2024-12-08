package com.ginger.democountryquizapp.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ginger.democountryquizapp.utils.FileUtils
import com.ginger.democountryquizapp.myApp.MyApplication
import com.ginger.democountryquizapp.model.Option
import com.ginger.democountryquizapp.model.Question
import org.json.JSONException
import org.json.JSONObject

class QuizViewModel : ViewModel() {

    val currentQuestion = MutableLiveData<Question>()
    val remainingTime = MutableLiveData<String>()
    val gameOver = MutableLiveData<Boolean>(false) // For tracking game over
    var score = 0
    var currentQuestionIndex = 0

    private lateinit var questionList: List<Question> // Parsed questions from JSON
    private var remainingSeconds: Long = 30
    private var timer: CountDownTimer? = null

    // SharedPreferences to save and load the game state
    private val preferences: SharedPreferences =
        MyApplication.getAppContext().getSharedPreferences("QuizPreferences", Context.MODE_PRIVATE)

    init {
        // Load the questions from the JSON or other sources
        loadQuestions()

        // Load the saved state (current question index and remaining time)
        loadState()
    }

    fun getQuestion(): LiveData<Question> = currentQuestion

    fun getRemainingTime(): LiveData<String> = remainingTime

    fun isGameOver(): LiveData<Boolean> = gameOver

    // Method to reset the game
    fun resetGame() {
        score = 0
        currentQuestionIndex = 0
        loadNextQuestion(currentQuestionIndex)
        remainingSeconds = 30 // Reset to 30 seconds for each question
        updateTimer(remainingSeconds)
        stopTimer()
        startTimer() // Start the timer again for the first question
        saveState() // Save the state after reset
    }

    // Load next question based on index
    fun loadNextQuestion(index: Int) {
        if (index < questionList.size) {
            currentQuestionIndex = index
            currentQuestion.value = questionList[index]
        } else {
            // Game over condition
            gameOver.value = true
        }
    }

    // Start the timer for countdown
    private fun startTimer() {
        timer = object : CountDownTimer(remainingSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingSeconds = millisUntilFinished / 1000
                updateTimer(remainingSeconds)
            }

            override fun onFinish() {
                remainingSeconds = 0
                updateTimer(remainingSeconds)
                markCurrentQuestionUnanswered()
            }
        }
        timer?.start()
    }

    // Stop the timer
    private fun stopTimer() {
        timer?.cancel()
    }

    // Update timer UI and save state
    fun updateTimer(secondsLeft: Long) {
        this.remainingSeconds = secondsLeft
        remainingTime.value = formatTime(secondsLeft)
        saveState() // Save the updated state
    }

    // Handle option selection for scoring
    fun handleOptionSelected(selectedOptionId: Int) {
        val question = currentQuestion.value
        question?.let {
            if (selectedOptionId == it.answerId) {
                score++
            }
            saveState() // Save the updated score
        }
    }

    // Mark the current question as unanswered
  /*  fun markCurrentQuestionUnanswered() {
        // Decrement score for unanswered questions (optional)
        if (score > 0) {
            score--
        }
        saveState() // Save the updated state

        // Move to the next question
        currentQuestionIndex++
        if (currentQuestionIndex < questionList.size) {
            loadNextQuestion(currentQuestionIndex)
        } else {
            // End of quiz logic
            gameOver.value = true
        }
    }*/

    fun markCurrentQuestionUnanswered() {
        // Do not decrement score for unanswered questions
        // Just move to the next question without affecting the score
        currentQuestionIndex++
        if (currentQuestionIndex < questionList.size) {
            loadNextQuestion(currentQuestionIndex)
        } else {
            // End of quiz logic
            gameOver.value = true
        }
    }



    // Load questions from the JSON file
    private fun loadQuestions() {
        val context = MyApplication.getAppContext()
        val jsonResponse = FileUtils.readJsonFromAssets(context, "questions.json")
        questionList = parseJsonResponse(jsonResponse)
    }

    // Parse the JSON response and create a list of questions
    private fun parseJsonResponse(jsonResponse: String): List<Question> {
        val questions = mutableListOf<Question>()
        try {
            val jsonObject = JSONObject(jsonResponse)
            val jsonQuestions = jsonObject.getJSONArray("questions")

            for (i in 0 until jsonQuestions.length()) {
                val jsonQuestion = jsonQuestions.getJSONObject(i)
                val answerId = jsonQuestion.getInt("answer_id")
                val countryCode = jsonQuestion.getString("country_code")

                val jsonCountries = jsonQuestion.getJSONArray("countries")
                val options = mutableListOf<Option>()
                for (j in 0 until jsonCountries.length()) {
                    val jsonCountry = jsonCountries.getJSONObject(j)
                    val id = jsonCountry.getInt("id")
                    val countryName = jsonCountry.getString("country_name")
                    options.add(Option(id, countryName))
                }

                questions.add(Question(answerId, countryCode, options))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return questions
    }

    // Format time to display as MM:SS
    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    // Load saved state from SharedPreferences
    private fun loadState() {
        // Load saved index and time
        currentQuestionIndex = preferences.getInt(KEY_CURRENT_QUESTION_INDEX, 0)
        remainingSeconds = preferences.getLong(KEY_REMAINING_SECONDS, 30)
        score = preferences.getInt(KEY_SCORE, 0)
        remainingTime.value = formatTime(remainingSeconds)

        // Load the next question based on saved index
        loadNextQuestion(currentQuestionIndex)
    }

    // Save current state (current question index, score, and remaining time) to SharedPreferences
    private fun saveState() {
        val editor = preferences.edit()
        editor.putInt(KEY_CURRENT_QUESTION_INDEX, currentQuestionIndex)
        editor.putLong(KEY_REMAINING_SECONDS, remainingSeconds)
        editor.putInt(KEY_SCORE, score)
        editor.apply()
    }

    // Keys for SharedPreferences
    companion object {
        private const val KEY_CURRENT_QUESTION_INDEX = "currentQuestionIndex"
        private const val KEY_REMAINING_SECONDS = "remainingSeconds"
        private const val KEY_SCORE = "score"
    }
}
