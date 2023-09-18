package com.example.vectonews.notification

data class NewsApiJSON(
    val news: List<News>,
    val page: Int,
    val status: String
)