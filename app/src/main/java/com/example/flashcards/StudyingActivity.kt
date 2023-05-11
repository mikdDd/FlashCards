package com.example.flashcards

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

class StudyingActivity : AppCompatActivity() {
    private lateinit var flashCardRecyclerView: RecyclerView
    private var flashCards: ArrayList<FlashCard>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning)
        flashCards = MainActivity.packageArrayList[intent.getIntExtra("position",0)].flashCards
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