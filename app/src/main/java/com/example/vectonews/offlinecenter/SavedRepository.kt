package com.example.vectonews.offlinecenter

import javax.inject.Inject

class SavedRepository @Inject constructor(private val savedDatabase: SavedDatabase) {

    val allNotes = savedDatabase.noteDao().getAllNotes()

    suspend fun insert(note: SavedModel) {
        savedDatabase.noteDao().insert(note)
    }


    suspend fun delete(note: SavedModel) {
        savedDatabase.noteDao().delete(note)
    }


}

