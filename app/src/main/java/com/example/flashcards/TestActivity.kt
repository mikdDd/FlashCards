package com.example.flashcards

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TestActivity : AppCompatActivity() {
    private var flashCards: ArrayList<FlashCard>? = null
    private lateinit var flashCardsToTest : ArrayList<FlashCard>
    private lateinit var nrAmount : TextView
    private lateinit var notionDefinition : TextView
    private lateinit var input : EditText
    private var goodAnswer = ""
    private var correctAnswers = 0
    private var wrongAnswers = 0
    private var globalCounter = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        flashCards = MainActivity.packageArrayList[intent.getIntExtra("position",0)].flashCards
        flashCardsToTest = flashCards as ArrayList<FlashCard>
        initWidgets()
        flashCardsToTest.shuffle()
        startTest()
    }

    private fun initWidgets() {
        nrAmount = findViewById(R.id.test_counter)
        notionDefinition = findViewById(R.id.notion_definition)
        input = findViewById(R.id.input)
    }

    @SuppressLint("SetTextI18n")
    private fun startTest() {
        if (globalCounter - 1 != flashCardsToTest.size) {
            nrAmount.text = "$globalCounter / ${flashCardsToTest.size}"
            notionDefinition.text = flashCardsToTest[globalCounter - 1].word
            goodAnswer = flashCardsToTest[globalCounter - 1].translation
        }
        else {
            val intent = Intent(this, TestScoreActivity::class.java)
            intent.putExtra("Good", correctAnswers)
            intent.putExtra("Wrong", wrongAnswers)
            startActivity(intent)
            finish()
        }
    }

    fun onSubmitClick(view: View) {
        if (input.text.toString() == goodAnswer) {
            correctAnswers++
        }
        else {
            wrongAnswers++
        }
        input.text.clear()
        globalCounter++
        startTest()
    }

    fun onDontKnowClick(view: View) {
        wrongAnswers++
        globalCounter++
        startTest()
    }
}
