package com.example.vectonews.api

import java.io.Serializable

data class UnsplashPhoto(
    val title: String?,
    val url: String?,
    val urlToImage: String?
): Serializable