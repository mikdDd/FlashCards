package com.example.flashcards

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class FlashCard(
    var word: String,
    var translation: String
) : Serializable
{
    @PrimaryKey(autoGenerate = true) var id: Long = 0
    var packageId: Int = 0
    var learned: Boolean = false
    //TODO
}