package com.example.flashcards

import android.os.Parcelable
import java.io.Serializable

class FlashCard(var word: String, var translation: String, var learned: Boolean = false ) : Serializable {

}