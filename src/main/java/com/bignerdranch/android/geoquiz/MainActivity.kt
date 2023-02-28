package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.bignerdranch.android.geoquiz.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by viewModels()
    private var cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            quizViewModel.isCheater =
                result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trueButton.setOnClickListener {
            quizViewModel.addAnswer()
            checkAnswer(true)
            setEnableButtons()
        }
        binding.falseButton.setOnClickListener {
            quizViewModel.addAnswer()
            checkAnswer(false)
            setEnableButtons()
        }
        binding.prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQuestion()
            setEnableButtons()
        }
        binding.nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
            setEnableButtons()
        }

        binding.cheatButton.setOnClickListener {
            // Start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }

        binding.questionTextView.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        updateQuestion()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurCheatButton()
        }
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        var message: String
        val color: Int

        when {
            quizViewModel.isCheater -> {
                message = getString(R.string.judgment_toast)
                color = Color.WHITE
            }
            userAnswer == correctAnswer -> {
                message = getString(R.string.correct_toast)
                color = Color.GREEN
                quizViewModel.score++
            }
            else -> {
                message = getString(R.string.incorrect_toast)
                color = Color.RED
            }
        }

        showSnackbar(message, color)

        if(quizViewModel.answers.size == quizViewModel.bankSize) {
            message = "${(quizViewModel.score / quizViewModel.bankSize * 100).toInt()}%"
            showSnackbar(message, Color.YELLOW)
            resetData()
        }
    }

    private fun setEnableButtons() {
        if (quizViewModel.isAnswered()) {
            binding.trueButton.isEnabled = false
            binding.falseButton.isEnabled = false
        } else {
            binding.trueButton.isEnabled = true
            binding.falseButton.isEnabled = true
        }
    }

    private fun resetData() {
        quizViewModel.answers.clear()
        quizViewModel.score = 0.0
        setEnableButtons()
    }

    private fun showSnackbar(text: String, color: Int) {
        val snackbar = Snackbar.make(findViewById(R.id.parent_view), text, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view
        val textView = sbView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        textView.setTextColor(color)

        snackbar.show()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun blurCheatButton() {
        val effect = RenderEffect.createBlurEffect(
            10.0f,
            10.0f,
            Shader.TileMode.CLAMP
        )
        binding.cheatButton.setRenderEffect(effect)
    }
}