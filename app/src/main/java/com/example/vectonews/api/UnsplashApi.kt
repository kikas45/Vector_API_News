package com.example.vectonews.api

import com.example.vectonews.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface UnsplashApi {

    companion object {
        const val BASE_URL = "https://newsapi.org/v2/"
        const val API_KEY = BuildConfig.NEWS_API_ACCESS_KEY
    }


//    @Headers("X-Api-Key: $API_KEY")
//    @GET("top-headlines")
//    suspend fun getRequstNews(
//        @Query("country") country: String, // Pass the country as a query parameter
//        @Query("category") category: String, // Pass the category as a query parameter
//        @Query("q") query: String,
//        @Query("page") page: Int,
//        @Query("pageSize") pageSize: Int
//    ): NewsResponses
//
//
//




    @Headers("Content-Type: application/json")
    @GET("top-headlines")
    suspend fun getRequstNews(
        @Header("X-Api-Key") apiKey: String,
        @Query("country") country: String,
        @Query("category") category: String,
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): NewsResponses





    @Headers("Content-Type: application/json")
    @GET("everything")
    suspend fun searchNews(
        @Header("X-Api-Key") apiKey: String,
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): NewsResponses






//    @Headers("X-Api-Key: $API_KEY")           // this  header is required by the Api , see more in documentation
//    @GET("everything")
//    suspend fun searchNews(
//        @Query("q") query: String,
//        @Query("page") page: Int,
//        @Query("pageSize") pageSize: Int
//    ): NewsResponses








}