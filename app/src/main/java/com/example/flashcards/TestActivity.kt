package com.example.flashcards

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TestActivity : AppCompatActivity() {
    private val ANSWER_AS_TRANSLATION = 1
    private val ANSWER_AS_NOTION = 2
    private var flashCards: ArrayList<FlashCard>? = null
    private var flashCardsToTest : ArrayList<FlashCard> = ArrayList()
    private lateinit var nrAmount : TextView
    private lateinit var notionDefinition : TextView
    private lateinit var input : EditText
    private var goodAnswer = ""
    private var correctAnswers = 0
    private var wrongAnswers = 0
    private var globalCounter = 1
    private var mode = 0
    private var isTestWithLearnedWords = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        mode = intent.getIntExtra("test_mode", 0)
        isTestWithLearnedWords = intent.getBooleanExtra("learned_mode", false)
        flashCards = MainActivity.packageArrayList[intent.getIntExtra("position_test",0)].flashCards
        if (!isTestWithLearnedWords) {
            for (i in 0 until flashCards!!.size) {
                if (!flashCards!![i].learned) {
                    flashCardsToTest.add(flashCards!![i])
                }
            }
        }
        else {
            for (i in 0 until flashCards!!.size) {
                flashCardsToTest.add(flashCards!![i])
            }
        }
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
            if (mode == ANSWER_AS_TRANSLATION) {
                notionDefinition.text = flashCardsToTest[globalCounter - 1].word
                goodAnswer = flashCardsToTest[globalCounter - 1].translation
            }
            else if (mode == ANSWER_AS_NOTION){
                notionDefinition.text = flashCardsToTest[globalCounter - 1].translation
                goodAnswer = flashCardsToTest[globalCounter - 1].word
            }
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
            Toast.makeText(this, "GOOD ANSWER!", Toast.LENGTH_LONG).show()
            for (i in 0 until flashCards!!.size) {
                if (mode == ANSWER_AS_TRANSLATION && flashCardsToTest[globalCounter - 1].id == flashCards!![i].id && flashCards!![i].translation == goodAnswer) {
                    MainActivity.packageArrayList[intent.getIntExtra("position_test",0)].flashCards[i].learned = true
                    val job = GlobalScope.launch(Dispatchers.IO)
                    {
                        MainActivity.dao.updateCard(MainActivity.packageArrayList[intent.getIntExtra("position_test",0)].flashCards[i])
                    }
                    runBlocking {
                        job.join()
                    }
                    break
                }
                else if (mode == ANSWER_AS_NOTION && flashCardsToTest[globalCounter - 1].id == flashCards!![i].id && flashCards!![i].word == goodAnswer) {
                    MainActivity.packageArrayList[intent.getIntExtra("position_test",0)].flashCards[i].learned = true
                    val job = GlobalScope.launch(Dispatchers.IO)
                    {
                        MainActivity.dao.updateCard(MainActivity.packageArrayList[intent.getIntExtra("position_test",0)].flashCards[i])
                    }
                    runBlocking {
                        job.join()
                    }
                    break
                }
            }
            correctAnswers++
        }
        else {
            Toast.makeText(this, "WRONG! IT WAS: $goodAnswer", Toast.LENGTH_LONG).show()
            for (i in 0 until flashCards!!.size) {
                if (mode == ANSWER_AS_TRANSLATION && flashCardsToTest[globalCounter - 1].id == flashCards!![i].id && flashCards!![i].translation == goodAnswer) {
                    MainActivity.packageArrayList[intent.getIntExtra("position_test",0)].flashCards[i].learned = false
                    break
                }
                else if (mode == ANSWER_AS_NOTION && flashCardsToTest[globalCounter - 1].id == flashCards!![i].id && flashCards!![i].word == goodAnswer) {
                    MainActivity.packageArrayList[intent.getIntExtra("position_test",0)].flashCards[i].learned = false
                    break
                }
            }
            wrongAnswers++
        }
        input.text.clear()
        globalCounter++
        startTest()
    }

    fun onDontKnowClick(view: View) {
        Toast.makeText(this, "WRONG! IT WAS: $goodAnswer", Toast.LENGTH_LONG).show()
        for (i in 0 until flashCards!!.size) {
            if (mode == ANSWER_AS_TRANSLATION && flashCardsToTest[globalCounter - 1].id == flashCards!![i].id && flashCards!![i].translation == goodAnswer) {
                MainActivity.packageArrayList[intent.getIntExtra("position_test",0)].flashCards[i].learned = false
                break
            }
            else if (mode == ANSWER_AS_NOTION && flashCardsToTest[globalCounter - 1].id == flashCards!![i].id && flashCards!![i].word == goodAnswer) {
                MainActivity.packageArrayList[intent.getIntExtra("position_test",0)].flashCards[i].learned = false
                break
            }
        }

        wrongAnswers++
        globalCounter++
        startTest()
    }
}
