package com.example.flashcards

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
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
import kotlinx.coroutines.runBlocking
import java.util.Date
import java.util.concurrent.TimeUnit


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
        val context = this.applicationContext





        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent()


                intent.putExtra("new_name", nameEditText.text.toString())
                intent.putExtra("position", packageListPosition)

                setResult(RESULT_OK,intent)
                finish()
            }
        })

        fbDataBase = Firebase.database.reference

        packageId = intent.getLongExtra("packageId",0).toInt()
        packageListPosition = intent.getIntExtra("position",0)
        flashCards = MainActivity.packageArrayList[packageListPosition].flashCards

        flashCardsListRecyclerView = findViewById(R.id.flash_cards_list_recycler)
        flashCardsListRecyclerView?.layoutManager = LinearLayoutManager(this)

        nameEditText = findViewById(R.id.editTextText)
        nameEditText.setText(MainActivity.packageArrayList[packageListPosition].name)

        nameEditText.setOnFocusChangeListener { _, hasFocus ->

            if(!hasFocus)
            {
                MainActivity.packageArrayList[packageListPosition].name = nameEditText.text.toString()
                val job = GlobalScope.launch(Dispatchers.IO)
                {
                    MainActivity.dao.updatePackage(MainActivity.packageArrayList[packageListPosition])
                }
                runBlocking {
                    job.join()
                }
            }
        }

        val adapter = flashCards?.let{FlashCardsListRecyclerAdapter(it,
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
                    flashCards!![position].learned = !flashCards!![position].learned
                    val job = GlobalScope.launch(Dispatchers.IO) {
                        MainActivity.dao.updateCard(it[position])
                    }

                    flashCardsListRecyclerView?.adapter?.notifyItemChanged(position)

                    runBlocking {
                        job.join()
                    }

                    flashCardsListRecyclerView?.adapter?.notifyItemChanged(position)

                }
        })}
        flashCardsListRecyclerView?.adapter = adapter
    }

    fun addFlashCardButtonClicked(view: View) {
        val intent = Intent(this, AddFlashCardActivity::class.java)
        addFlashCardResultLauncher.launch(intent)

    }
    private var addFlashCardResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val data: Intent? = result.data
            val wordString = data?.getStringExtra("word").toString()
            val translationString = data?.getStringExtra("translation").toString()

            val flashCard = FlashCard( wordString,translationString)
            flashCard.packageId = packageId
            val job = GlobalScope.launch(Dispatchers.IO)
            {
                flashCard.id = MainActivity.dao.insertCard(flashCard)
            }
            flashCards?.add(flashCard)
            runBlocking {
                job.join()
            }
            flashCards?.size?.let { flashCardsListRecyclerView?.adapter?.notifyItemInserted(it) }

        }


    }

    fun onUploadButtonClick(view: View) {


        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("SHARING PACKAGE "+ MainActivity.packageArrayList[packageListPosition].name)
        alertDialog.setMessage("SHARE CODE: $lastGeneratedCode (will be active for 2hrs)")
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "OK"
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()



        fbDataBase.child("packages").get().addOnSuccessListener {
            if(it.child(lastGeneratedCode).exists()){
                val pack = PackageFireBaseAdapter(MainActivity.packageArrayList[packageListPosition], ServerValue.TIMESTAMP)
                fbDataBase.child("packages").child(lastGeneratedCode).setValue(pack)
                alertDialog.setMessage("SHARE CODE: $lastGeneratedCode (will be active for 2hrs)")
            } else{
                val randStr = String.format("%04d",(0..9999).random())

                if(!it.child(randStr).exists()){
                    val pack = PackageFireBaseAdapter(MainActivity.packageArrayList[packageListPosition], ServerValue.TIMESTAMP)
                    fbDataBase.child("packages").child(randStr).setValue(pack)
                    lastGeneratedCode = randStr
                    alertDialog.setMessage("SHARE CODE: $lastGeneratedCode (will be active for 2hrs)")

                } else{
                    Toast.makeText(this,"TRY AGAIN",Toast.LENGTH_LONG).show()
                }
            }
        }






            //TODO: poprawić
        val cutoff : Long = Date().time - TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS)
        Log.i("MMM", "CUTOFF:$cutoff")


        val oldItems: Query = FirebaseDatabase.getInstance().reference.child("packages").orderByChild("timestamp").endBefore(cutoff.toDouble())
        oldItems.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (itemSnapshot in snapshot.children) {
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

    fun onPackageDeleteButtonClick(view: View)
    {
        val intent = Intent()
        val job = GlobalScope.launch(Dispatchers.IO) {
            Log.d("test321", packageListPosition.toString())
            MainActivity.dao.deletePackage(MainActivity.packageArrayList[packageListPosition])
            MainActivity.dao.deleteAllCards(MainActivity.packageArrayList[packageListPosition].id)
        }
        runBlocking {
            job.join()
        }
        MainActivity.packageArrayList.removeAt(packageListPosition)

        intent.putExtra("position",packageListPosition)
        intent.putExtra("deleted",true)

        setResult(RESULT_OK,intent)

        finish()
    }
}


