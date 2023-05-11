package com.example.flashcards

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FlashCardsListActivity : AppCompatActivity() {

    private var flashCards: ArrayList<FlashCard>? = null
    private var flashCardsListRecyclerView: RecyclerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_cards_list)

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            flashCards = intent.getParcelableArrayListExtra("flash_card_list",FlashCard::class.java) as ArrayList<FlashCard>?
            Log.i("MMM","TU")
        } else {
            flashCards = intent.getParcelableExtra("flash_card_list")
        }
        Log.i("MMM", flashCards.toString())
         */
        flashCards = MainActivity.packageArrayList[intent.getIntExtra("position",0)].flashCards
        flashCardsListRecyclerView = findViewById(R.id.flash_cards_list_recycler)
        flashCardsListRecyclerView?.layoutManager = LinearLayoutManager(this)
        var adapter = flashCards?.let{FlashCardsListRecyclerAdapter(it, object:FlashCardsListRecyclerAdapter.DeleteButtonListener{
            override fun onDeleteButtonClick(position: Int) {
                it.removeAt(position)
                flashCardsListRecyclerView?.adapter?.notifyItemRemoved(position)
            }
        })}

        flashCardsListRecyclerView?.adapter = adapter
    }

    fun addFlashCardButtonClicked(view: View) {
        val intent = Intent(this, AddFlashCardActivity::class.java)
        resultLauncher.launch(intent)

    }
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val data: Intent? = result.data
            val wordString = data?.getStringExtra("word").toString()
            val translationString = data?.getStringExtra("translation").toString()
            flashCards?.add(FlashCard(wordString,translationString))

            flashCards?.size?.let { flashCardsListRecyclerView?.adapter?.notifyItemInserted(it) }
        }


    }


}
//TODO learned checkbox pass by intent
//TODO edycja fiszki