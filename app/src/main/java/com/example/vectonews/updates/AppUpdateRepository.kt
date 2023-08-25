package com.example.vectonews.updates

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class AppUpdateRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    fun getAppUpdate(onSuccess: (AppUpdate?) -> Unit, onFailure: (Exception) -> Unit) {
        val docRef = firestore.collection("App").document("Update")

        docRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val version = document.getString("version")
                    val date_publish = document.getString("date_publish")
                    val url = document.getString("url")

                    val appUpdate = AppUpdate(version, date_publish, url)
                    onSuccess(appUpdate)
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}

