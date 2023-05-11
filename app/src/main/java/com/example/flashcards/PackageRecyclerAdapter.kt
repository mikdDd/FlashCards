package com.example.flashcards

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PackageRecyclerAdapter(private val packageSet: ArrayList<Package>,
                             private val editButtonListener: EditButtonListener
                            ) : RecyclerView.Adapter<PackageRecyclerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val packageNameTextView: TextView
        val numOfFlashCardsTextView: TextView
        val editButton: ImageButton
        val playButton: ImageButton

        init {
            packageNameTextView = view.findViewById(R.id.package_name_textView)
            numOfFlashCardsTextView = view.findViewById(R.id.num_textView)
            editButton = view.findViewById(R.id.edit_button)
            playButton = view.findViewById(R.id.play_button)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.package_item, viewGroup, false)
        return ViewHolder(view)
    }



    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.i("TTT",packageSet[position].packageName)
        viewHolder.packageNameTextView.text = packageSet[position].packageName
        viewHolder.editButton.setOnClickListener { editButtonListener.onEditButtonClick(position) }
    }

    override fun getItemCount() = packageSet.size

    interface EditButtonListener{
        fun onEditButtonClick(position: Int)
    }
}