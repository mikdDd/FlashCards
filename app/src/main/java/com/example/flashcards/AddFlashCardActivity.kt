package com.example.flashcards

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddFlashCardActivity : AppCompatActivity() {

    private var wordEditText: EditText? = null
    private var translationEditText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_add_flash_card)
        wordEditText = findViewById(R.id.word_editText)
        translationEditText = findViewById(R.id.translation_editText)
    }

    fun onAddButtonClick(view: View) {
        val intent = Intent()
        intent.putExtra("word",wordEditText?.text.toString())
        intent.putExtra("translation",translationEditText?.text.toString())
        setResult(RESULT_OK,intent)
        finish()
    }
}