package com.example.vectonews.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.vectonews.api.UnsplashApi
import javax.inject.Inject

class UnsplashRepository @Inject constructor(val newsApi: UnsplashApi) {


    fun getSearchResults(query: String, country: String) =
        Pager(
            config = PagingConfig(
                pageSize = 6,
                maxSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UnsplashPagingSource(newsApi, query, country) }
        ).liveData
}