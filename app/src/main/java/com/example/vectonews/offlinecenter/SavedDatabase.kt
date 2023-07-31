package com.example.vectonews.offlinecenter

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.vectonews.api.UnsplashPhoto

@Database(entities = [UnsplashPhoto::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SavedDatabase : RoomDatabase() {

    abstract fun noteDao(): SavedDao



}