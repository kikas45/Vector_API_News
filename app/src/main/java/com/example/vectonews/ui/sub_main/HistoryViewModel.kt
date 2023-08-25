package com.example.vectonews.ui.sub_main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.vectonews.api.ApiKeyRepository
import com.example.vectonews.data.SearchNewsUnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: SearchNewsUnsplashRepository,
    private val apiKeyRepository: ApiKeyRepository,
) : ViewModel() {

    val apiKeyLiveData = MutableLiveData<String>()
    private val currentQuery = MutableLiveData<String>()


    init {
        apiKeyRepository.fetchApiKey(apiKeyLiveData)
    }


    val photos = apiKeyLiveData.switchMap { apiKey ->
        currentQuery.switchMap { queryString ->
            repository.getSearchResults(apiKey, queryString).cachedIn(viewModelScope)
        }
    }

    fun searchNews(query: String) {
        currentQuery.value = query
    }


}
