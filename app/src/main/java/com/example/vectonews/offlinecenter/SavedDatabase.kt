package com.example.vectonews.offlinecenter

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SavedModel::class], version = 1, exportSchema = false )
abstract class SavedDatabase: RoomDatabase() {
    abstract fun noteDao(): SavedDao
}