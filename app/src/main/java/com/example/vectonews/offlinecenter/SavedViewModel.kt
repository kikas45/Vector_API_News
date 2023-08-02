package com.example.vectonews.offlinecenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vectonews.api.UnsplashPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SavedViewModel  @Inject constructor(private val noteRepository: SavedRepository) : ViewModel() {

    val allNotes: LiveData<List<UnsplashPhoto>> = noteRepository.allNotes

    fun insert(note: UnsplashPhoto) {

      ///  note.isSaved = true
        viewModelScope.launch {
            noteRepository.insert(note)
        }
    }

    fun update(note: UnsplashPhoto) {
        viewModelScope.launch {
            noteRepository.update(note)
        }
    }

    fun delete(note: UnsplashPhoto) {

     //   note.isSaved = false
        viewModelScope.launch {
            noteRepository.delete(note)
        }
    }


    fun toggleSavedStatus(savedModel: UnsplashPhoto) {
        viewModelScope.launch {
            val updatedModel = savedModel.copy(isSaved = !savedModel.isSaved)
            noteRepository.update(updatedModel)
        }
    }



}
