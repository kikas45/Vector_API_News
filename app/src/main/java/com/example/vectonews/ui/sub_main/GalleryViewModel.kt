package com.example.vectonews.ui.sub_main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.vectonews.data.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(private val repository: UnsplashRepository, savedStateHandle: SavedStateHandle,) : ViewModel() {

    private val currentQuery = savedStateHandle.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)

    val photos = currentQuery.switchMap { queryString ->
        repository.getSearchResults(queryString).cachedIn(viewModelScope)
    }


    companion object {
        private const val CURRENT_QUERY = "current_query"  // we use this one for process death
        private const val DEFAULT_QUERY = ""
    }


}