package com.example.vectonews.api

import java.io.Serializable

data class UnsplashPhoto(
    val publishedAt: String,
    val source: Source,
    val title: String?,
    val url: String?,
    val urlToImage: String?
): Serializable