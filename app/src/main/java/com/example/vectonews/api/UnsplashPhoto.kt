package com.example.vectonews.api

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
@Entity(tableName = "saved_art")
data class UnsplashPhoto(
    @PrimaryKey
    val title: String,
    val url: String?,
    val urlToImage: String?,
    val source: Source,
    val publishedAt: String?,
    var isSaved: Boolean = false
): Serializable