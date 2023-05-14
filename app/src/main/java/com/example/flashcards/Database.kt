package com.example.flashcards

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FlashCard::class, Package::class], version = 1)
abstract class Database : RoomDatabase()
{
    abstract fun Dao(): DatabaseDao
}
