package com.example.flashcards

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Package(
    var name: String
)
{
    @PrimaryKey(autoGenerate = true) var id: Long = 0
    @Ignore var flashCards: ArrayList<FlashCard> = ArrayList()
}