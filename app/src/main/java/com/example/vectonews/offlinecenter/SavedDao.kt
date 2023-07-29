package com.example.vectonews.offlinecenter

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SavedDao {

    @Insert(onConflict = (OnConflictStrategy.REPLACE))
    fun insert(savedModel: SavedModel)

    @Delete
    fun delete(savedModel: SavedModel)

    @Query("SELECT * FROM  saved_art ORDER BY title ASC LIMIT 30")
    fun  getAllNotes():LiveData<List<SavedModel>>
}