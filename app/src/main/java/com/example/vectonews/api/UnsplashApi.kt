package com.example.vectonews.api

import com.example.vectonews.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UnsplashApi {

    companion object {
        const val BASE_URL = "https://newsapi.org/v2/"
        const val API_KEY = BuildConfig.NEWS_API_ACCESS_KEY
    }


    @Headers("X-Api-Key: $API_KEY")
    @GET("top-headlines")
    suspend fun searchPhotos(
        @Query("country") country: String, // Pass the country as a query parameter
        @Query("category") category: String, // Pass the category as a query parameter
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): NewsResponses
}