package com.example.vectonews.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.signIn.GoogleSignInViewModel
import com.example.vectonews.MainActivity
import com.example.vectonews.R
import com.example.vectonews.databinding.FragmentMainHomeBinding
import com.example.vectonews.settings.AppSettings
import com.example.vectonews.util.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.log

@AndroidEntryPoint
class Main_Home_Fragment : Fragment(R.layout.fragment_main_home) {

    private val commentKey = "commentKey"
    private val commentsArticles = "AllCommentsArticles"


    private lateinit var navController: NavController

    private val settings: AppSettings by lazy {
        AppSettings(requireContext().applicationContext)
    }


    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val sharedPref: SharedPreferences by lazy {
        requireContext().getSharedPreferences(Constants.USER_PROFILE, Context.MODE_PRIVATE)

    }



    private lateinit var googlesignInClient: GoogleSignInClient
    private val googleSignInViewModel: GoogleSignInViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googlesignInClient = GoogleSignIn.getClient(requireContext(), gso)


        val filter = IntentFilter(Constants.Main_Home_Fragment)
        requireContext().registerReceiver(NavigationBroadcastReceiver, filter)


        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragmentChildContainer) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView =
            view.findViewById<BottomNavigationView>(R.id.bottomChildNavigationView)
        bottomNavigationView.setupWithNavController(navController)


        changeToolbarColor()

        autoDeleteOldComments()


    }


    // receiving navigation command from Home and Save fragment
    private val NavigationBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val navControl = getNavController(context)

            val newCountry = intent.getStringExtra("Navigation")
            val titles = intent.getStringExtra("titles") // for web view
            val getUrl = intent.getStringExtra("getUrl") // for web view

            if (newCountry.equals("Navigate_to_SearchHistoryFragment")) {
                try {
                    navControl.navigate(R.id.action_main_Home_Fragment_to_main_Save_Fragment)
                } catch (_: Exception) {
                }
            } else if (newCountry.equals("Navigate_To_Detail_Fragment_From_Search_Fragment")) {
                try {
                    val bundle = Bundle().apply {
                        putString("urls_webView", getUrl.toString())
                        putString("titles", titles.toString()) // for web view
                    }

                    navControl.navigate(R.id.action_searchFragment_to_detailFragment, bundle)
                } catch (_: Exception) {
                }

            } else if (newCountry.equals("signOutWithGoogle")) {

                try {
                    googlesignInClient.signOut().addOnCompleteListener {
                        if (it.isSuccessful) {
                            sharedPref.edit().clear().apply()
                        }
                    }
                } catch (_: Exception) {
                }


            } else if (newCountry.equals("signWithGoogle")) {
              try {
                signinWithGoogle()
              } catch (_: Exception) { }

            } else {

                val bundle = Bundle().apply {

                    putString("urls_webView", getUrl.toString())
                    putString("titles", titles.toString()) // for web view

                }

                navControl.navigate(R.id.action_main_Home_Fragment_to_detailFragment, bundle)
            }
        }

        private fun getNavController(context: Context): NavController {
            val navHostFragment = (context as AppCompatActivity).supportFragmentManager
                .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            return navHostFragment.navController
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            requireContext().unregisterReceiver(NavigationBroadcastReceiver)
        } catch (_: Exception) {
        }

    }


    @SuppressLint("ObsoleteSdkInt")
    private fun changeToolbarColor() {

        try {
            if (settings.getTheme() == AppSettings.THEME_LIGHT) {
                activity?.window?.statusBarColor = Color.parseColor("#FFFFFF")

            } else {
                activity?.window?.statusBarColor = Color.parseColor("#1E1D1D")

            }
        } catch (_: Exception) {
        }

    }

    private fun signinWithGoogle() {

        val siginIntent = googlesignInClient.signInIntent
        mLauncher.launch(siginIntent)
    }

    private val mLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                googleSignInViewModel.handleGoogleSignInResult(task)

                if (task.isSuccessful){
                    showToast("Account Created")
                }
            }

        }


    private fun showToast(message: String) {
        try {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        } catch (ignored: Exception) {
        }
    }


    private fun autoDeleteOldComments() {
        val database = FirebaseDatabase.getInstance()
        val dataRef = database.reference.child(commentKey)


         val thirtySecondsAgo = System.currentTimeMillis() - 30000

       //  val twoDaysAgo = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000

      //  val thirtyMinutesAgo = System.currentTimeMillis() - 30 * 60 * 1000

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { childSnapshot ->
                    val createdAt = childSnapshot.child("createdAt").getValue(String::class.java)
                    val articleId = childSnapshot.child("articleId").getValue(String::class.java)

                    if (createdAt != null) {
                        val createdAtMillis = convertTimestampToMillis(createdAt)
                        if (createdAtMillis < thirtySecondsAgo) {
                            deleteMyNewsArticles(articleId.toString())

                            childSnapshot.ref.removeValue()

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


    private fun convertTimestampToMillis(timestamp: String): Long {
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        val date = dateFormat.parse(timestamp)
        return date?.time ?: 0L
    }


    private fun deleteMyNewsArticles(articleId: String) {
        val database = FirebaseDatabase.getInstance()
        val dataRef = database.reference.child(commentsArticles)
        dataRef.child(articleId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                showToast("Deleted Successfully")
            }
        }
    }


}