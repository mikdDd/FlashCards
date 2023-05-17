package com.example.flashcards

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TestScoreActivity : AppCompatActivity() {
    lateinit var quitButton : ImageButton
    private lateinit var progressCircle : ProgressBar
    lateinit var scoreTV : TextView
    lateinit var goodAnswersTV : TextView
    lateinit var wrongAnswersTV : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_score)
        initWidgets()
        showScore()
    }

    private fun initWidgets() {
        quitButton = findViewById(R.id.quitScoreButton)
        progressCircle = findViewById(R.id.final_score)
        scoreTV = findViewById(R.id.progress_text)
        goodAnswersTV = findViewById(R.id.good_answers)
        wrongAnswersTV = findViewById(R.id.wrong_answers)
    }

    @SuppressLint("SetTextI18n")
    private fun showScore() {
        val goodAnswers = intent.getIntExtra("Good", 0)
        val wrongAnswers = intent.getIntExtra("Wrong", 0)
        goodAnswersTV.text = "Good answers: $goodAnswers"
        wrongAnswersTV.text = "Wrong answers: $wrongAnswers"
        val ratio = goodAnswers.toDouble() / (goodAnswers.toDouble() + wrongAnswers.toDouble())
        progressCircle.setProgress((ratio * 100).toInt(), false)
        scoreTV.text = "${(ratio * 100).toInt()}%"
    }

    fun onQuitClick(view: View) {
        finish()
    }
}