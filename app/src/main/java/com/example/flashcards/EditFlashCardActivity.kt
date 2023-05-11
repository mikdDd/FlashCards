package com.example.flashcards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.EditText

class EditFlashCardActivity : AppCompatActivity() {

    private var wordEditText: EditText? = null
    private var translationEditText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_edit_flash_card)
        wordEditText = findViewById(R.id.word_editText)
        translationEditText = findViewById(R.id.translation_editText)
        wordEditText?.setText(intent.getStringExtra("word"))
        translationEditText?.setText(intent.getStringExtra("translation"))
    }

    fun onAddButtonClick(view: View) {
        val intent = Intent()
        intent.putExtra("word",wordEditText?.text.toString())
        intent.putExtra("translation",translationEditText?.text.toString())
        setResult(RESULT_OK,intent)
        finish()
    }
}