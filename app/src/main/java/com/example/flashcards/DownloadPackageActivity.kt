package com.example.flashcards

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


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
        var name = ""
        val list = ArrayList<FlashCard>()

        fbDataBase.child("packages").child(editTextNumber.text.toString()).child("package_").child("name").get().addOnSuccessListener {

            if(it.exists()){
                Log.i("MMM",it.value.toString())
                name = it.value.toString()
                Toast.makeText(this,"PACKAGE DOWNLOADED SUCCESSFULLY",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,"UNABLE TO DOWNLOAD PACKAGE",Toast.LENGTH_SHORT).show()
                finish()
            }

        }

        fbDataBase.child("packages").child(editTextNumber.text.toString()).child("package_").child("flashCards").get().addOnSuccessListener { it ->
            if (it.exists()) {


            for (snap in it.children) {
                val word = snap.child("word").value as String
                val translation = snap.child("translation").value as String


                val fc = FlashCard(word, translation)
                list.add(fc)
                Log.i("MMM", "LEARNED: " + snap.child("learned").value)
            }

            Package(name)
                .let {
                    GlobalScope.launch(Dispatchers.IO)
                    {
                        it.id = MainActivity.dao.insertPackage(it)
                        //Log.d("test321", it.id.toString())
                        for (elem in list) {
                            elem.packageId = it.id.toInt()
                            elem.id = MainActivity.dao.insertCard(elem)
                        }
                    }

                    //TODO: Zmiana packageId w fiszkach (niby zrobione wyżej ale trzeba sprawdzić)
                    it.flashCards = list



                    MainActivity.packageArrayList.add(it)
                    setResult(RESULT_OK, intent)
                    Log.d("test321", "furgrevj")
                    finish()

                }
        } }
    }
}