package com.example.vectonews.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.example.vectonews.api.UnsplashApi
import com.example.vectonews.api.UnsplashPhoto
import com.google.android.gms.common.api.internal.ApiKey
import java.io.IOException

private const val UNSPLASH_STARTING_PAGE_INDEX = 1


class PagingSourceHistory(
    private val unsplashApi: UnsplashApi,
    private val apiKey:String,
    private val query: String,
) : PagingSource<Int, UnsplashPhoto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val position = params.key ?: UNSPLASH_STARTING_PAGE_INDEX

        return try {
            val response = unsplashApi.searchNews(apiKey, query, position, params.loadSize)
            val photos = response.articles

            LoadResult.Page(
                data = photos,
                prevKey = if (position == UNSPLASH_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UnsplashPhoto>): Int? {
        TODO("Not yet implemented")
    }
}