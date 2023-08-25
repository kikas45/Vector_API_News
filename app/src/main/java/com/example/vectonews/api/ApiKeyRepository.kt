package com.example.vectonews.api

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ApiKeyRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    fun fetchApiKey(liveData: MutableLiveData<String>) {
        val docRef = firestore.collection("AppKey").document("api")

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val apiKey = document.getString("key")
                    liveData.postValue(apiKey)
                }
            }
            .addOnFailureListener {
                liveData.value = ""
            }
    }
}
