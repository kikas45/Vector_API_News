package com.example.vectonews.offlinecenter

import javax.inject.Inject

class SavedRepository @Inject constructor( private  val savedDatabase: SavedDatabase) {

    //  val allNotes: LiveData<List<UnsplashPhoto>> = savedDatabase.noteDao().getAllNotes()

    val allNotes = savedDatabase.noteDao().getAllNotes()

    suspend fun insert(note: SavedModel) {
        savedDatabase.noteDao().insert(note)
    }

    suspend fun update(note: SavedModel) {
        savedDatabase.noteDao().update(note)
    }

    suspend fun delete(note: SavedModel) {
        savedDatabase.noteDao().delete(note)
    }


}

