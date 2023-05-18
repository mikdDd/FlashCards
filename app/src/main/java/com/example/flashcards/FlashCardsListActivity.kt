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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FlashCardsListActivity : AppCompatActivity() {

    private var flashCards: ArrayList<FlashCard>? = null
    private var flashCardsListRecyclerView: RecyclerView? = null
    private var packageId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_cards_list)
        var context = this.applicationContext

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            flashCards = intent.getParcelableArrayListExtra("flash_card_list",FlashCard::class.java) as ArrayList<FlashCard>?
            Log.i("MMM","TU")
        } else {
            flashCards = intent.getParcelableExtra("flash_card_list")
        }
        Log.i("MMM", flashCards.toString())
         */
        packageId = intent.getLongExtra("packageId", 0L)
        Log.d("test321", packageId.toString())
        flashCards = MainActivity.packageArrayList[intent.getIntExtra("position",0)].flashCards
        flashCardsListRecyclerView = findViewById(R.id.flash_cards_list_recycler)
        flashCardsListRecyclerView?.layoutManager = LinearLayoutManager(this)
        var adapter = flashCards?.let{FlashCardsListRecyclerAdapter(it, object:FlashCardsListRecyclerAdapter.DeleteButtonListener{
            override fun onDeleteButtonClick(position: Int) {
                val job = GlobalScope.launch(Dispatchers.IO)
                {
                    MainActivity.dao.deleteCard(it.removeAt(position))
                }

                flashCardsListRecyclerView?.adapter?.notifyItemRemoved(position)

                runBlocking {
                    job.join()
                }

            }
        },object:FlashCardsListRecyclerAdapter.EditButtonListener{
            var pos : Int? = null
            override fun onEditButtonClick(position: Int) {
                intent = Intent(context,EditFlashCardActivity::class.java)

                intent.putExtra("word",flashCards?.get(position)?.word)
                intent.putExtra("translation",flashCards?.get(position)?.translation)
                pos = position

                editResultLauncher.launch(intent)
            }
            var editResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {

                    val data: Intent? = result.data
                    val wordString = data?.getStringExtra("word").toString()
                    val translationString = data?.getStringExtra("translation").toString()
                    flashCards?.get(pos!!)?.word = wordString
                    flashCards?.get(pos!!)?.translation = translationString


                    val job = GlobalScope.launch(Dispatchers.IO)
                    {
                        flashCards?.get(pos!!)?.let { it1 -> MainActivity.dao.updateCard(it1) }
                    }

                    runBlocking {
                        job.join()
                    }

                    flashCards?.let { flashCardsListRecyclerView?.adapter?.notifyItemChanged(pos!!)}
                }


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

            var flashCard = FlashCard(packageId, wordString,translationString)
            GlobalScope.launch(Dispatchers.IO)
            {
                flashCard.id = MainActivity.dao.insertCard(flashCard)
            }
            flashCards?.add(flashCard)

            flashCards?.size?.let { flashCardsListRecyclerView?.adapter?.notifyItemInserted(it) }
        }


    }


}
//TODO learned checkbox pass by intent
//TODO checkbox onclick