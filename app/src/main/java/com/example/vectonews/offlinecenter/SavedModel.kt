package com.example.vectonews.offlinecenter

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_art")
data class SavedModel(
    @PrimaryKey
    val title: String,
    val url: String,
    val urlToImage: String,
)