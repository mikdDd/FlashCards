package com.example.flashcards

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView

class FlashCardsListRecyclerAdapter(private val flashCardsList: ArrayList<FlashCard>,
                                    private val deleteButtonListener: DeleteButtonListener,
                                    private val editButtonListener: EditButtonListener,
                                    private val checkBoxListener: CheckBoxListener
                                    ) : RecyclerView.Adapter<FlashCardsListRecyclerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var wordTextView : TextView
        var translationTextView : TextView
        var learnedCheckBox : CheckBox
        var editButton: ImageButton
        var deleteButton: ImageButton
        init {
            wordTextView = view.findViewById(R.id.word_textView)
            translationTextView = view.findViewById(R.id.translation_textView)
            learnedCheckBox = view.findViewById(R.id.learned_checkBox)
            editButton = view.findViewById(R.id.edit_button)
            deleteButton = view.findViewById(R.id.delete_button)
            learnedCheckBox.isChecked = false
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.flashcard_list_item, viewGroup, false)
        return ViewHolder(view)
    }



    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.wordTextView.text = flashCardsList[position].word
        viewHolder.translationTextView.text = flashCardsList[position].translation
        viewHolder.deleteButton.setOnClickListener { deleteButtonListener.onDeleteButtonClick(position) }
        viewHolder.editButton.setOnClickListener { editButtonListener.onEditButtonClick(position) }
        viewHolder.learnedCheckBox.setOnClickListener { checkBoxListener.onCheckBoxClick(position) }
    }

    override fun getItemCount() = flashCardsList.size

    interface DeleteButtonListener{
        fun onDeleteButtonClick(position: Int)
    }

    interface EditButtonListener{
        fun onEditButtonClick(position: Int)
    }

    interface CheckBoxListener {
        fun onCheckBoxClick(position: Int)
    }
}