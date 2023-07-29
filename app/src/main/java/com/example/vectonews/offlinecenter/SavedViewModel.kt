package com.example.vectonews.offlinecenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(private  val  repository: SavedRepository):ViewModel() {

    val allNotes : LiveData<List<SavedModel>> = repository.allNotes

    fun  insert (savedModel: SavedModel){
        viewModelScope.launch {
            repository.insert(savedModel)
        }

    }

    fun delete(savedModel: SavedModel){
        viewModelScope.launch {
            repository.delete(savedModel)
        }
    }

}