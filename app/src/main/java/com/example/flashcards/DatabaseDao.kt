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

    @Query("UPDATE FlashCard SET learned = :newValue WHERE id = :flashCardID")
    fun updateLearned(flashCardID: Long, newValue: Boolean)

    @Update
    fun updateCard(card: FlashCard)

    @Delete
    fun deleteCard(card: FlashCard)
/*
    @Query("SELECT * FROM flashCards, packages WHERE packages.name=':cardPackage' AND package.id=flashCards.package_id")
    fun getCardsFromPackage(cardPackage: Package): List<FlashCard>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<FlashCard>

    @Insert
    fun insertAll(card: FlashCard)

    @Delete
    fun delete(card: FlashCard)
*/

}