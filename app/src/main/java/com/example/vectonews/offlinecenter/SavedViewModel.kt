package com.example.vectonews.offlinecenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel  @Inject constructor(private val noteRepository: SavedRepository) : ViewModel() {

    val allNotes: LiveData<List<SavedModel>> = noteRepository.allNotes

    fun insert(note: SavedModel) {
        viewModelScope.launch {
            noteRepository.insert(note)
        }
    }

    fun update(note: SavedModel) {
        viewModelScope.launch {
            noteRepository.update(note)
        }
    }

    fun delete(note: SavedModel) {
        viewModelScope.launch {
            noteRepository.delete(note)
        }
    }



}
