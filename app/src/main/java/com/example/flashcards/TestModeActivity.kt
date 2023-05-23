package com.example.flashcards

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("UseSwitchCompatOrMaterialCode")
class TestModeActivity : AppCompatActivity() {
    private var position : Int = 0
    private var withLearnedOn : Boolean = false
    private lateinit var switch : Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_mode)
        position = intent.getIntExtra("position", 0)
        switch = findViewById(R.id.learnedSwitch)
        switch.setOnCheckedChangeListener { _, isChecked ->
            withLearnedOn = isChecked
        }
    }

    fun setAnswerAsTranslation(view: View) {
        val intent = Intent(this, TestActivity::class.java)
        intent.putExtra("test_mode",1)
        intent.putExtra("learned_mode", withLearnedOn)
        intent.putExtra("position", position)
        startActivity(intent)
        finish()
    }

    fun setAnswerAsNotion(view: View) {
        val intent = Intent(this, TestActivity::class.java)
        intent.putExtra("test_mode",2)
        intent.putExtra("learned_mode", withLearnedOn)
        intent.putExtra("position", position)
        startActivity(intent)
        finish()
    }
}