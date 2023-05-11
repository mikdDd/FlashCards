package com.example.flashcards

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.motion.widget.FloatLayout
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class FlashCardStudyingAdapter(private val flashCards: ArrayList<FlashCard>?)
    : RecyclerView.Adapter<FlashCardStudyingAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val flashCardButton : Button
        val number : TextView
        init {
            flashCardButton = view.findViewById(R.id.flashCard)
            number = view.findViewById(R.id.nr)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.flashcard_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return flashCards?.size ?: 0
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val index = position + 1
        holder.number.text = "$index / ${flashCards?.size}"
        holder.flashCardButton.text = flashCards?.get(position)?.word
        holder.flashCardButton.setOnClickListener {
            if (holder.flashCardButton.text == flashCards?.get(position)?.word) {
                holder.flashCardButton.text = flashCards?.get(position)?.translation
            }
            else {
                holder.flashCardButton.text = flashCards?.get(position)?.word
            }
        }
    }
}