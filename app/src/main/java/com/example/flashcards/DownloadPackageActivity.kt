package com.example.flashcards

import android.content.ClipData.Item
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flashcards.Package
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.security.AccessController.getContext
import java.time.Duration
import javax.security.auth.login.LoginException


class DownloadPackageActivity : AppCompatActivity() {
    private lateinit var fbDataBase: DatabaseReference
    private lateinit var editTextNumber :EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_download_package)


        fbDataBase = FirebaseDatabase.getInstance().reference
        editTextNumber = findViewById(R.id.editTextNumber)
    }

    fun onDownloadButtonClick(view: View) {




        val intent = Intent()



        var name: String = ""
        var list = ArrayList<FlashCard>()

        var empty : Boolean =false
        fbDataBase.child("packages").child(editTextNumber.text.toString()).get().addOnSuccessListener {
            if(!it.exists()){
                Log.i("MMM","PUSTE")
                empty = true
                Log.i("MMM",empty.toString())
            }
        }



        fbDataBase.child("packages").child(editTextNumber.text.toString()).child("package_").child("name").get().addOnSuccessListener {

            if(it.exists()){
                Log.i("MMM",it.value.toString())
                name = it.value.toString()
                Toast.makeText(this,"PACKAGE DOWNLOADED SUCCESSFULLY",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,"UNABLE TO DOWNLOAD PACKAGE",Toast.LENGTH_SHORT).show()
                //TODO: KOMUNIKAT
                finish()
            }

        }

        fbDataBase.child("packages").child(editTextNumber.text.toString()).child("package_").child("flashCards").get().addOnSuccessListener {
            if (it.exists()) {


            for (snap in it.children) {
                //name  = snap.child("name").value as String
                var word = snap.child("word").value as String
                var translation = snap.child("translation").value as String
                Log.i("MMM", word + translation)

                var fc = FlashCard(word, translation)
                list.add(fc)
                Log.i("MMM", "LEARNED: " + snap.child("learned").value)
            }

            //MainActivity.packageArrayList.add(com.example.flashcards.Package(name))
            Package(name)
                .let {
                    GlobalScope.launch(Dispatchers.IO)
                    {
                        it.id = MainActivity.dao.insertPackage(it)
                        //Log.d("test321", it.id.toString())
                        for (elem in list) {
                            elem.packageId = it.id.toInt()
                        }
                        Log.i("MMM", "ID1: " + it.id)
                    }
                    Log.i("MMM", "ID2: " + it.id)


                    //TODO: Zmiana packageId w fiszkach (niby zrobione wyżej ale trzeba sprawdzić)
                    it.flashCards = list

                    MainActivity.packageArrayList.add(it)
                    setResult(RESULT_OK, intent)
                    finish()
                    //packageRecyclerView?.adapter?.notifyItemInserted(MainActivity.packageArrayList.size)

                }
        }
        }



    }

}