package com.example.vectonews.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/*
@HiltViewModel
class CommentViewModel @Inject constructor(repository: CommentRepository ):ViewModel() {


    private val _newsFeedLiveData = MutableLiveData<List<ModelComment>>()
    val newsFeedLiveData: LiveData<List<ModelComment>> = _newsFeedLiveData


    init { repository.fetchComments(_newsFeedLiveData) }



}
*/




@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repository: CommentRepository
) : ViewModel() {

    private val _newsFeedLiveData = MutableLiveData<List<ModelComment>>()
    val newsFeedLiveData: LiveData<List<ModelComment>> = _newsFeedLiveData

    init {
        fetchComments()
    }

    private fun fetchComments() {
        repository.fetchComments(_newsFeedLiveData)
    }




    fun saveComment(commentData: HashMap<String, String>, callback: (Boolean, String?) -> Unit) {
        repository.saveNewsArticleComment(commentData) { isSuccess, pushKey ->
            callback(isSuccess, pushKey)
        }
    }


    fun updateCommentField(newsArticleId: String, commentKey: String, fieldKey: String, fieldValue: String) {
        repository.updateCommentField(newsArticleId, commentKey, fieldKey, fieldValue) { isSuccess ->
            // Handle the result if needed
        }
    }




    fun deleteComment(newsArticleId: String, commentKey: String, callback: (Boolean) -> Unit) {
        repository.deleteComment(newsArticleId, commentKey) { isSuccess ->
            callback(isSuccess)
        }
    }


    fun checkTotalComments(newsArticleId: String, callback: (Boolean) -> Unit) {
        repository.checkTotalComments(newsArticleId, callback)
    }

    fun checkIfCommentKeyExists(newsArticleId: String, callback: (Boolean) -> Unit) {
        repository.checkIfCommentKeyExists(newsArticleId, callback)
    }



}

