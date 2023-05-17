package com.example.flashcards

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PackageRecyclerAdapter(private val packageSet : ArrayList<Package>,
                             private val editButtonListener : EditButtonListener,
                             private val studyButtonListener : StudyButtonListener,
                             private val testButtonListener : TestButtonListener
                            ) : RecyclerView.Adapter<PackageRecyclerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val packageNameTextView: TextView
        val numOfFlashCardsTextView: TextView
        val editButton: ImageButton
        val studyButton: ImageButton
        val testButton: ImageButton

        init {
            packageNameTextView = view.findViewById(R.id.package_name_textView)
            numOfFlashCardsTextView = view.findViewById(R.id.num_textView)
            editButton = view.findViewById(R.id.edit_button)
            studyButton = view.findViewById(R.id.study_button)
            testButton = view.findViewById(R.id.test_button)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.package_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.i("TTT",packageSet[position].name)
        viewHolder.packageNameTextView.text = packageSet[position].name
        viewHolder.editButton.setOnClickListener { editButtonListener.onEditButtonClick(position) }
        viewHolder.studyButton.setOnClickListener { studyButtonListener.onStudyButtonClick(position)}
        viewHolder.testButton.setOnClickListener { testButtonListener.onTestButtonClick(position) }
    }

    override fun getItemCount() = packageSet.size

    interface EditButtonListener{
        fun onEditButtonClick(position: Int)
    }

    interface StudyButtonListener {
        fun onStudyButtonClick(position: Int)
    }

    interface TestButtonListener {
        fun onTestButtonClick(position: Int)
    }
}