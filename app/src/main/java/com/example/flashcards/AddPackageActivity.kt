package com.example.flashcards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText

class AddPackageActivity : AppCompatActivity() {

    private var nameEditText: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_add_package)
        nameEditText = findViewById(R.id.name_editText)

    }

    fun onAddButtonClicked(view: View) {
        val intent = Intent()
        Log.i("TTT",nameEditText?.text.toString())
        intent.putExtra("package_name",nameEditText?.text.toString())
        setResult(RESULT_OK,intent)
        finish()
    }
}
//TODO liczba nauczonych/wielkosc pakietu