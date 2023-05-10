package com.example.flashcards

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

class StudyingActivity : AppCompatActivity() {
    private lateinit var flashCardRecyclerView: RecyclerView
    //TODO: Get flashcards from the folder
    private var flashCards = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning)
        flashCards.add("jeden")
        flashCards.add("dwa")
        flashCards.add("trzy")
        flashCards.add("cztery")
        flashCards.add("pięć")
        initWidgets()
        setFlashCardView()
    }

    private fun initWidgets() {
        flashCardRecyclerView = findViewById(R.id.flashCardRecyclerView)
    }

    @SuppressLint("SetTextI18n")
    private fun setFlashCardView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        flashCardRecyclerView.layoutManager = layoutManager
        flashCardRecyclerView.setHasFixedSize(true)
        val flashCardAdapter = FlashCardStudyingAdapter(flashCards)
        flashCardRecyclerView.adapter = flashCardAdapter
        PagerSnapHelper().attachToRecyclerView(flashCardRecyclerView)
    }
}