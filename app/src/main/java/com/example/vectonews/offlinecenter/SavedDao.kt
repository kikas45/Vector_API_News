package com.example.vectonews.offlinecenter
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SavedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: SavedModel)

    @Update
    suspend fun update(note: SavedModel)

    @Delete
    suspend fun delete(note: SavedModel)



    @Query("SELECT * FROM saved_art  ORDER BY title ASC LIMIT 30")
    fun getAllNotes(): LiveData<List<SavedModel>>



}