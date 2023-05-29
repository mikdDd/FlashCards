package com.example.flashcards

import androidx.room.*

@Dao
interface DatabaseDao
{
    @Insert
    fun insertPackage(newPackage: Package): Long

    @Query("SELECT * FROM Package")
    fun selectAllPackages() : List<Package>

    @Update
    fun updatePackage(updatedPackage: Package)

    @Delete
    fun deletePackage(oldPackage: Package)


    @Insert
    fun insertCard(card: FlashCard): Long

    @Query("SELECT * FROM FlashCard WHERE packageId = :packageID")
    fun selectAllCards(packageID: Long) : List<FlashCard>

    @Update
    fun updateCard(card: FlashCard)

    @Delete
    fun deleteCard(card: FlashCard)

    @Query("DELETE FROM FlashCard WHERE packageId = :packageID")
    fun deleteAllCards(packageID: Long)
}