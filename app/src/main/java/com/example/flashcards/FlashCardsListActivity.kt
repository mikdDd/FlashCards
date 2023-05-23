package com.example.flashcards

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

  import java.util.Date
import java.util.concurrent.TimeUnit

import kotlinx.coroutines.runBlocking


class FlashCardsListActivity : AppCompatActivity() {

    private var flashCards: ArrayList<FlashCard>? = null
    private var flashCardsListRecyclerView: RecyclerView? = null

    private var packageId = 0
    private var packageListPosition : Int = 0
    private lateinit var fbDataBase: DatabaseReference
    private var lastGeneratedCode: String = "0"
    private lateinit var nameEditText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_cards_list)
        var context = this.applicationContext




        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent()

                intent.putExtra("new_name", nameEditText.text.toString())
                intent.putExtra("position", packageListPosition)
                //println("Back button pressed")
                setResult(RESULT_OK,intent)
                finish()
                // Code that you need to execute on back press i.e. finish()
            }
        })

        fbDataBase = Firebase.database.reference

        packageId = intent.getIntExtra("packageId",0)
        packageListPosition = intent.getIntExtra("position",0)

        flashCards = MainActivity.packageArrayList[packageListPosition].flashCards
        Log.i("MMM","FC: " + flashCards)

        flashCardsListRecyclerView = findViewById(R.id.flash_cards_list_recycler)
        flashCardsListRecyclerView?.layoutManager = LinearLayoutManager(this)

        nameEditText = findViewById(R.id.editTextText)
        nameEditText.setText(MainActivity.packageArrayList[packageListPosition].name)

        var adapter = flashCards?.let{FlashCardsListRecyclerAdapter(it,
            object:FlashCardsListRecyclerAdapter.DeleteButtonListener{
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
        }, object: FlashCardsListRecyclerAdapter.CheckBoxListener {
                override fun onCheckBoxClick(position: Int) {
                    val job = GlobalScope.launch(Dispatchers.IO) {
                        MainActivity.dao.updateLearned(position.toLong(), !it[position].learned)
                        it[position].learned = !it[position].learned
                    }

                    flashCardsListRecyclerView?.adapter?.notifyItemChanged(position)

                    runBlocking {
                        job.join()
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

            var flashCard = FlashCard( wordString,translationString)
            flashCard.packageId = packageId
            GlobalScope.launch(Dispatchers.IO)
            {
                flashCard.id = MainActivity.dao.insertCard(flashCard)
            }
            flashCards?.add(flashCard)

            flashCards?.size?.let { flashCardsListRecyclerView?.adapter?.notifyItemInserted(it) }
        }


    }

    fun onUploadButtonClick(view: View) {


       // var randInt = 1250
        var randInt = (0..9999).random()

       /// println("%5d",randInt)

        var success : Boolean = false

        //fbDataBase.child("packages").eq
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("SHARING PACKAGE "+MainActivity.packageArrayList.get(packageListPosition).name)
        alertDialog.setMessage("SHARE CODE: $lastGeneratedCode (will be active for 2hrs)")
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "OK"
        ) { dialog, which -> dialog.dismiss() }
        alertDialog.show()



        fbDataBase.child("packages").get().addOnSuccessListener {
            if(it.child(lastGeneratedCode).exists()){
                Log.i("MMM","EXIST")
                val pack = PackageFireBaseAdapter(MainActivity.packageArrayList.get(packageListPosition), ServerValue.TIMESTAMP)
                fbDataBase.child("packages").child(lastGeneratedCode).setValue(pack)
                alertDialog.setMessage("SHARE CODE: $lastGeneratedCode (will be active for 2hrs)")
            } else{
                Log.i("MMM","NOTEXIST")
                var randStr = String.format("%04d",(0..9999).random())

                if(!it.child(randStr).exists()){
                    val pack = PackageFireBaseAdapter(MainActivity.packageArrayList.get(packageListPosition), ServerValue.TIMESTAMP)
                    fbDataBase.child("packages").child(randStr).setValue(pack)
                    lastGeneratedCode = randStr
                    alertDialog.setMessage("SHARE CODE: $lastGeneratedCode (will be active for 2hrs)")

                } else{
                    Log.i("MMM","KOD ZAJĘTY ")
                }
            }
        }






            //TODO: poprawić
        val cutoff : Long = Date().time - TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS)
        Log.i("MMM", "CUTOFF:$cutoff")


        val oldItems: Query = FirebaseDatabase.getInstance().getReference().child("packages").orderByChild("timestamp").endBefore(cutoff.toDouble())
        oldItems.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (itemSnapshot in snapshot.children) {
                        Log.i("MMM","REMOVING" + itemSnapshot.value)


                        if(itemSnapshot.key!=lastGeneratedCode){
                            itemSnapshot.ref.removeValue()
                        }

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        })

    }

    fun onPackageDeleteButtonClick(view: View) {
        MainActivity.packageArrayList.removeAt(packageListPosition)
        val intent = Intent()
        intent.putExtra("position",packageListPosition)
        intent.putExtra("deleted",true)
        //intent.putExtra("new_name", nameEditText.text.toString())
        setResult(RESULT_OK,intent)
        finish()

    }


}

//TODO learned checkbox pass by intent

//TODO learned checkbox pass by intent
//TODO checkbox onclick

