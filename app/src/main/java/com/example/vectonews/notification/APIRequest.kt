package com.example.vectonews.notification

import retrofit2.http.GET

interface APIRequest {

   // @GET("/v1/latest-news?language=en&apiKey=Pgt9u_oDwERVatBnBVDJiwY5wE-YP9mDqt23YlRPJhAPhIq6")
    @GET("/v1/latest-news?category=entertainment?language=en&apiKey=Pgt9u_oDwERVatBnBVDJiwY5wE-YP9mDqt23YlRPJhAPhIq6")

    suspend fun getNews(): NewsApiJSON


}