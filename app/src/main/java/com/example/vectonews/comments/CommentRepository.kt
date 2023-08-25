package com.example.vectonews.comments

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.example.vectonews.util.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject



class CommentRepository @Inject constructor(private val firebase: FirebaseDatabase, private val pref: SharedPreferences) {

    fun fetchComments(liveData: MutableLiveData<List<ModelComment>>) {
        val newsArticleId = pref.getString("newsArticleId", "")!!
        val database = firebase.getReference("AllCommentsArticles")
        database.child(newsArticleId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val newsFeedItems: List<ModelComment> = snapshot.children.map { dataSnapshot ->
                        dataSnapshot.getValue(ModelComment::class.java)!!

                    }

                    liveData.postValue(newsFeedItems)
                }else{
                    liveData.value = null
                }
            }

            override fun onCancelled(error: DatabaseError) {
                liveData.value = null
            }
        })
    }



    fun saveNewsArticleComment(commentData: HashMap<String, String>, callback: (Boolean, String?) -> Unit) {
        val newsArticleId = pref.getString("newsArticleId", "")!!
        val database = firebase.getReference("AllCommentsArticles")

        val newCommentRef = database.child(newsArticleId).push()
        newCommentRef.setValue(commentData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, newCommentRef.key.toString())
                } else {
                    callback(false, null)
                }
            }
    }



    fun deleteComment(newsArticleId: String, commentKey: String, callback: (Boolean) -> Unit) {
        val database = firebase.getReference("AllCommentsArticles")

        database.child(newsArticleId).child(commentKey).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }


    fun updateCommentField(newsArticleId: String, commentKey: String, fieldKey: String, fieldValue: String, callback: (Boolean) -> Unit) {
        val database = firebase.getReference("AllCommentsArticles")

        val updateData = hashMapOf<String, Any>(fieldKey to fieldValue)
        database.child(newsArticleId).child(commentKey).updateChildren(updateData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }




    fun checkTotalComments(newsArticleId: String, callback: (Boolean) -> Unit) {
        val dataRef = firebase.getReference(Constants.commentsArticles)
        dataRef.child(newsArticleId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val counts = snapshot.childrenCount
                callback(counts <= 80)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }

    fun checkIfCommentKeyExists(newsArticleId: String, callback: (Boolean) -> Unit) {
        val dataRef = firebase.getReference(Constants.commentKey)
        dataRef.child(newsArticleId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(!snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }




}




