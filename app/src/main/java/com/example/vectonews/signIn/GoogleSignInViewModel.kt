package com.example.signIn

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class GoogleSignInViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var editor: SharedPreferences.Editor


    private val sharedPref: SharedPreferences by lazy {
        application.applicationContext.getSharedPreferences(USER_PROFILE, Context.MODE_PRIVATE)

    }

    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.result
                account?.let { signInWithGoogleAccount(it) }
            } else {
                // Handle error
            }
        } catch (e: Exception) {
            // Handle exception
        }
    }

    private fun signInWithGoogleAccount(account: GoogleSignInAccount) {
        val credentials: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credentials).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                saveUserProfile(account)
            }
        }
    }

    private fun saveUserProfile(account: GoogleSignInAccount) {
        val user = UserProfile(
            account.displayName.toString(),
            account.email.toString(),
            account.photoUrl.toString(),
            account.id.toString()
        )

        saveUserProfiles(
            account.email.toString(),
            account.displayName.toString(),
            account.photoUrl.toString(),
            account.id.toString()
        )

        val userId = account.id.toString()
        val userRef = firebaseDatabase.getReference("User").child(userId)
        userRef.setValue(user).addOnCompleteListener { dbTask ->
            if (dbTask.isSuccessful) {
                // Handle success
            } else {
                // Handle database error
            }
        }
    }

    private fun saveUserProfiles(
        userEmail: String,
        userName: String,
        userImage: String,
        userId: String,
    ) {

        editor = sharedPref.edit()
        editor.putString("userEmail", userEmail)
        editor.putString("userName", userName)
        editor.putString("userImage", userImage)
        editor.putString("userId", userId)
        editor.apply()
    }

    companion object{
        private const   val USER_PROFILE = "User_Profiles"

    }
}
