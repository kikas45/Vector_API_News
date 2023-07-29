package com.example.vectonews.util

import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class RateLimitInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 429) {
            // Handle the 429 error here by retrying the request after a delay
            val retryAfter = response.headers["Retry-After"]
            if (!retryAfter.isNullOrBlank()) {
                try {
                    val seconds = retryAfter.toInt()
                    Thread.sleep(TimeUnit.SECONDS.toMillis(seconds.toLong()))
                    return chain.proceed(request) // Retry the request
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

        return response
    }
}
