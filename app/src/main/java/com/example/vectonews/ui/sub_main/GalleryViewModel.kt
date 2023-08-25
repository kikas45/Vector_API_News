package com.example.vectonews.ui.sub_main


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.vectonews.api.ApiKeyRepository
import com.example.vectonews.data.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: UnsplashRepository,
    private val apiKeyRepository: ApiKeyRepository,
    savedStateHandle: SavedStateHandle,

) : ViewModel() {


    val apiKeyLiveData = MutableLiveData<String>()

    init {
        apiKeyRepository.fetchApiKey(apiKeyLiveData)
    }


    private val currentQuery = savedStateHandle.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)
    private val currentCountry = MutableLiveData(DEFAULT_COUNTRY)
    private val currentCategory = MutableLiveData(DEFAULT_CATEGORY)


    val photos = currentQuery.switchMap { queryString ->
        apiKeyLiveData.switchMap { apiKey ->
            currentCountry.switchMap { country ->
                currentCategory.switchMap { category ->
                    repository.getNewsRequest(queryString, apiKey, country, category)
                        .cachedIn(viewModelScope)
                }
            }
        }
    }




    // Add functions to update the current country and category
    fun updateCountry(country: String) {
        currentCountry.value = country
    }

    fun updateCategory(category: String) {
        currentCategory.value = category
    }


    companion object {
        private const val CURRENT_QUERY = "current_query" // we are using this for process death purpose
        private const val DEFAULT_QUERY = ""
        private const val DEFAULT_COUNTRY = "......"  //  us , ng, sa, ca,  but a default parameter must be passed
        private const val DEFAULT_CATEGORY = ""  // you can pass parameters like sport, business, entertainment, by default

    }




}
