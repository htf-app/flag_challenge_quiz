package com.ginger.democountryquizapp.activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ginger.democountryquizapp.R
import com.ginger.democountryquizapp.databinding.ActivityQuizBinding
import com.ginger.democountryquizapp.model.Question
import com.ginger.democountryquizapp.viewmodel.QuizViewModel

class QuizActivity : AppCompatActivity() {

    private var isPaused: Boolean?=null
    private lateinit var binding: ActivityQuizBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val viewModel: QuizViewModel by viewModels()

    private var currentQuestionIndex = 0
    private var QuizStarted = 0
    private var questionTimer: CountDownTimer? = null
    private var isAnswerSelected = false
    private var remainingTime = 30 // Default time for each question
    private var elapsedTime=0L
    private var exit_time=0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("QuizState", MODE_PRIVATE)

        // Restore state if it exists
        if (sharedPreferences.getBoolean("quiz_in_progress", false)) {
            restoreQuizState()
        }

        // Observe ViewModel properties to update UI when data changes
        viewModel.getQuestion().observe(this, ::loadQuestion)
        viewModel.remainingTime.observe(this) { binding.timerText.text = it.toString() }

        // Set click listeners for option buttons
        binding.option1.setOnClickListener { handleOptionSelection(0) }
        binding.option2.setOnClickListener { handleOptionSelection(1) }
        binding.option3.setOnClickListener { handleOptionSelection(2) }
        binding.option4.setOnClickListener { handleOptionSelection(3) }

        // Start quiz
    }

    override fun onPause() {
        super.onPause()
        isPaused=true
        saveQuizState()
        questionTimer?.cancel() // Stop the timer
    }

    override fun onResume() {
        super.onResume()
        if (isPaused==true){
            restoreQuizState()
            elapsedTime =System.currentTimeMillis()-exit_time
            elapsedTime=elapsedTime/1000
            val index=elapsedTime/10
            currentQuestionIndex= (currentQuestionIndex+index).toInt()
            Log.d(currentQuestionIndex.toString(),"questionIndex")
            isPaused=false
        }
        if (remainingTime > 0) {
            startQuestionTimer(remainingTime)
        }
        startQuiz()
    }

    override fun onDestroy() {
        super.onDestroy()
        isPaused=true
    }


    private fun saveQuizState() {
        val editor = sharedPreferences.edit()
        editor.putInt("current_question_index", currentQuestionIndex)
        editor.putInt("remaining_time", remainingTime)
        editor.putLong("exit_time", System.currentTimeMillis())
        editor.putBoolean("quiz_in_progress", true)
        editor.putInt("QuizStarted", 1)
        editor.putBoolean("isPaused", true)
        editor.apply()
        Log.d(System.currentTimeMillis().toString(),"currentQuestionIndexNext")

    }

    private fun restoreQuizState() {
        currentQuestionIndex = sharedPreferences.getInt("current_question_index", 0)
        QuizStarted = sharedPreferences.getInt("QuizStarted", 0)
        isPaused = sharedPreferences.getBoolean("isPaused", false)
        remainingTime = sharedPreferences.getInt("remaining_time", 30)
        exit_time = sharedPreferences.getLong("exit_time", 0L)
    }

    private fun loadQuestion(question: Question?) {
        if (question == null) return

        // Update question number and flag
        binding.questionNumber.text = "${currentQuestionIndex + 1}/15"
        binding.flagImage.setImageResource(getFlagResource(question.countryCode))

        // Set text for options
        binding.option1.text = question.options[0].countryName
        binding.option2.text = question.options[1].countryName
        binding.option3.text = question.options[2].countryName
        binding.option4.text = question.options[3].countryName

        // Reset UI for the new question
        resetOptionColors()
        resetFeedbackTexts()
        isAnswerSelected = false
    }

    private fun startQuiz() {
        if (currentQuestionIndex < 15) {
            startQuestionTimer(remainingTime)
            viewModel.loadNextQuestion(currentQuestionIndex)
        } else {
            showGameOver()
        }
    }

    private fun startQuestionTimer(seconds: Int) {
        questionTimer?.cancel() // Cancel any existing timer
        questionTimer = object : CountDownTimer((seconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                viewModel.updateTimer(secondsLeft.toLong())
                remainingTime = secondsLeft
                binding.questionNumber.text = "${currentQuestionIndex + 1}/15"
            }

            override fun onFinish() {
                // Mark the current question as unanswered (wrong answer)
                viewModel.markCurrentQuestionUnanswered()

                currentQuestionIndex++
                if (currentQuestionIndex < 15) {
                    remainingTime = 30 // Reset timer for the next question
                    saveQuizState() // Save progress

                    // Disable options right before the 10-second interval
                    disableOptions()

                    // Show interval before the next question
                    binding.timerText.text = getString(R.string.interval_text) // Show interval message

                    Handler(Looper.getMainLooper()).postDelayed({
                        // Enable options after the 10-second delay and start the next question
                        enableOptions()
                        startQuiz() // Start the next question
                    }, 5000) // 10-second interval
                } else {
                    // No more questions, finish the quiz
                    showGameOver()
                }
            }
        }.start() // Start the countdown timer
    }

    private fun disableOptions() {
        binding.option1.isEnabled = false
        binding.option2.isEnabled = false
        binding.option3.isEnabled = false
        binding.option4.isEnabled = false
    }

    private fun enableOptions() {
        binding.option1.isEnabled = true
        binding.option2.isEnabled = true
        binding.option3.isEnabled = true
        binding.option4.isEnabled = true
    }




    private fun showGameOver() {
        // Clear saved state
        sharedPreferences.edit().clear().apply()
        val intent = Intent(this, GameScoreActivity::class.java).apply {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("GameScore", viewModel.score)
        }
        startActivity(intent)
        viewModel.resetGame()
        finish()

    }

    private fun resetOptionColors() {
        binding.option1.setBackgroundResource(R.drawable.rect_bg)
        binding.option2.setBackgroundResource(R.drawable.rect_bg)
        binding.option3.setBackgroundResource(R.drawable.rect_bg)
        binding.option4.setBackgroundResource(R.drawable.rect_bg)
    }

    private fun resetFeedbackTexts() {
        binding.option1Feedback.visibility = View.GONE
        binding.option2Feedback.visibility = View.GONE
        binding.option3Feedback.visibility = View.GONE
        binding.option4Feedback.visibility = View.GONE
    }

    private fun handleOptionSelection(optionIndex: Int) {
        if (isAnswerSelected) return

        val question = viewModel.getQuestion().value ?: return
        val selectedOption = question.options[optionIndex]

        if (selectedOption.id == question.answerId) {
            highlightCorrectAnswer(optionIndex)
            viewModel.handleOptionSelected(selectedOption.id)
        } else {
            highlightIncorrectAnswer(optionIndex, question.answerId)
        }
        isAnswerSelected = true
    }

    private fun highlightCorrectAnswer(selectedOptionIndex: Int) {
        resetOptionColors()
        val correctText = getString(R.string.correct_text)

        when (selectedOptionIndex) {
            0 -> {
                binding.option1.setBackgroundResource(R.drawable.correct_answer)
                binding.option1Feedback.text = correctText
            }
            1 -> {
                binding.option2.setBackgroundResource(R.drawable.correct_answer)
                binding.option2Feedback.text = correctText
            }
            2 -> {
                binding.option3.setBackgroundResource(R.drawable.correct_answer)
                binding.option3Feedback.text = correctText
            }
            3 -> {
                binding.option4.setBackgroundResource(R.drawable.correct_answer)
                binding.option4Feedback.text = correctText
            }
        }
        showFeedbackText(correctText)
    }

    private fun highlightIncorrectAnswer(selectedOptionIndex: Int, correctAnswerId: Int) {
        val wrongText = getString(R.string.wrong_text)
        highlightOption(selectedOptionIndex, Color.RED, wrongText)

        val question = viewModel.getQuestion().value ?: return
        question.options.indexOfFirst { it.id == correctAnswerId }.takeIf { it >= 0 }?.let {
            highlightOption(it, Color.GREEN, getString(R.string.correct_text))
        }

        showFeedbackText(getString(R.string.wrong_feedback))
    }

    private fun highlightOption(optionIndex: Int, color: Int, feedbackText: String) {
        val (button, feedback) = when (optionIndex) {
            0 -> binding.option1 to binding.option1Feedback
            1 -> binding.option2 to binding.option2Feedback
            2 -> binding.option3 to binding.option3Feedback
            else -> binding.option4 to binding.option4Feedback
        }

        button.setBackgroundResource(if (feedbackText == getString(R.string.wrong_text)) R.drawable.wrong else R.drawable.correct_answer)
        feedback.visibility = View.VISIBLE
        feedback.text = feedbackText
        feedback.setTextColor(color)
    }

    private fun showFeedbackText(feedback: String) {
        // Additional UI updates if needed
    }

    private fun getFlagResource(countryCode: String): Int {
        return FLAG_MAP[countryCode] ?: R.drawable.ec
    }

    companion object {
        private val FLAG_MAP = mapOf(
            "NZ" to R.drawable.nz,
            "AW" to R.drawable.aw,
            "EC" to R.drawable.ec,
            "PY" to R.drawable.py,
            "KG" to R.drawable.kg,
            "PM" to R.drawable.pm,
            "JP" to R.drawable.jp,
            "TM" to R.drawable.tm,
            "GA" to R.drawable.ga,
            "MQ" to R.drawable.mq,
            "BZ" to R.drawable.bz,
            "CZ" to R.drawable.cz,
            "AE" to R.drawable.ae,
            "LS" to R.drawable.ls
        )
    }
}
