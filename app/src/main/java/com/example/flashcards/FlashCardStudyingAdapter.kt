package com.example.flashcards

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FlashCardStudyingAdapter(private val flashCards: ArrayList<String>) : RecyclerView.Adapter<FlashCardStudyingAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val flashCardTextView : TextView = view.findViewById(R.id.flashCard)
        private val number : TextView = view.findViewById(R.id.nr)
        @SuppressLint("SetTextI18n")
        fun setText(txt : String, position: Int, size : Int) {
            val index = position + 1
            flashCardTextView.text = txt
            number.text = "$index / $size"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.flashcard_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return flashCards.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setText(flashCards[position], position, flashCards.size)
    }
}