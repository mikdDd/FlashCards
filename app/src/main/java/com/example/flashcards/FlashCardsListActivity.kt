package com.example.flashcards

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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


class FlashCardsListActivity : AppCompatActivity() {

    private var flashCards: ArrayList<FlashCard>? = null
    private var flashCardsListRecyclerView: RecyclerView? = null
    private var packageId = 0
    private var packageListPosition : Int = 0
    private lateinit var fbDataBase: DatabaseReference
    private var lastGeneratedCode: String = "0"


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
        fbDataBase = Firebase.database.reference

        packageId = intent.getIntExtra("packageId",0)
        packageListPosition = intent.getIntExtra("position",0)
        flashCards = MainActivity.packageArrayList[packageListPosition].flashCards
        flashCardsListRecyclerView = findViewById(R.id.flash_cards_list_recycler)
        flashCardsListRecyclerView?.layoutManager = LinearLayoutManager(this)
        var adapter = flashCards?.let{FlashCardsListRecyclerAdapter(it, object:FlashCardsListRecyclerAdapter.DeleteButtonListener{
            override fun onDeleteButtonClick(position: Int) {
                it.removeAt(position)
                flashCardsListRecyclerView?.adapter?.notifyItemRemoved(position)
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


        fbDataBase.child("packages").addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(lastGeneratedCode).exists()) {
                    // run some code
                    Log.i("MMM","EXIST")
                    val pack = PackageFireBaseAdapter(MainActivity.packageArrayList.get(packageListPosition), ServerValue.TIMESTAMP)
                    fbDataBase.child("packages").child(lastGeneratedCode).setValue(pack)
                    alertDialog.setMessage("SHARE CODE: $lastGeneratedCode (will be active for 2hrs)")

                } else {
                    Log.i("MMM","NOTEXIST")
                    var randStr = String.format("%04d",(0..9999).random())

                    if(!snapshot.child(randStr).exists()){
                        val pack = PackageFireBaseAdapter(MainActivity.packageArrayList.get(packageListPosition), ServerValue.TIMESTAMP)
                        fbDataBase.child("packages").child(randStr).setValue(pack)
                        lastGeneratedCode = randStr
                        alertDialog.setMessage("SHARE CODE: $lastGeneratedCode (will be active for 2hrs)")

                    } else{
                        Log.i("MMM","KOD ZAJĘTY ")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })





    /*


        Log.i("MMM","STR: " + randStr)
        fbDataBase.child("packages").child().get().addOnSuccessListener {

            if(it.exists() && randStr != lastGeneratedCode) {
                Log.i("MMM", "ZAJETY PIN")
            } else{
                val pack = PackageFireBaseAdapter(MainActivity.packageArrayList.get(packageListPosition), ServerValue.TIMESTAMP)
                fbDataBase.child("packages").child(randStr).setValue(pack)
                lastGeneratedCode = randStr
            }
        }
        //Log.i("MMM",fbDataBase.child("packages").child("123"))
        //Log.i("MMM","$randInt")
  */



        val cutoff : Long = Date().time - TimeUnit.MILLISECONDS.convert(3, TimeUnit.SECONDS)
        Log.i("MMM", "CUTOFF:$cutoff")
        val oldItems: Query = FirebaseDatabase.getInstance().getReference().child("packages").orderByChild("timestamp").endBefore(cutoff.toDouble())
        oldItems.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (itemSnapshot in snapshot.children) {
                        Log.i("MMM","REMOVING" + itemSnapshot.value)
                        ///if(itemSnapshot.value() == lastGeneratedCode)
                        //Log.i("MMM","VAL:" + itemSnapshot.key)

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

    //9999
}
//TODO learned checkbox pass by intent