package com.example.vectonews

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.vectonews.notification.NotificationService
import com.example.vectonews.settings.AppSettings
import com.example.vectonews.updates.AppUpdateViewModel
import com.example.vectonews.util.Constants
import com.google.android.material.chip.Chip
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var receiver: MyBroadcastReceiver

    private lateinit var navController: NavController

    private val settings: AppSettings by lazy {
        AppSettings(applicationContext)
    }

    private val appUpdateViewModel: AppUpdateViewModel by viewModels()


    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }


    private val shared_do_no_show: SharedPreferences by lazy {
        applicationContext.getSharedPreferences(
            Constants.DO_NO_SHOW_AGAIN,
            Context.MODE_PRIVATE
        )
    }

    private val shared_time_count: SharedPreferences by lazy {
        applicationContext.getSharedPreferences(
            Constants.SHARED_TIME,
            Context.MODE_PRIVATE
        )
    }


    private val sharedPrefPassDataToDetailsFragment: SharedPreferences by lazy {
        applicationContext.getSharedPreferences(
            Constants.PASS_DATA_TO_DETAIL_FRAGMENT,
            Context.MODE_PRIVATE
        )

    }
    private val sharedPref: SharedPreferences by lazy {
        applicationContext.getSharedPreferences(Constants.USER_PROFILE, Context.MODE_PRIVATE)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        //   Thread.sleep(2000)  /// remember to remove this after production
        applyTheme(settings.getTheme())
        setContentView(R.layout.activity_main)
        setTheme(R.style.Base_Theme_VectorNews)
        supportActionBar?.hide()


        checkingForPendingIntent()



        val mFirebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true)


        app_update_statues()


        drawerLayout = findViewById(R.id.drawer_layout)

        val drawerLayoutRef = WeakReference(drawerLayout)
        receiver = MyBroadcastReceiver(drawerLayoutRef)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController


        val navigationView: NavigationView = findViewById(R.id.navigation_view)

        // inflate the drawer head
        val header = navigationView.inflateHeaderView(R.layout.drawer_header)

        val settings = header.findViewById<TextView>(R.id.textSettings)

        val textHeadLinesUs = header.findViewById<Chip>(R.id.textHeadLinesUs)
        val textHeadLinesNigeria = header.findViewById<Chip>(R.id.textHeadLinesNigeria)
        val textHeadLinesCanada = header.findViewById<Chip>(R.id.textHeadLinesCanada)
        val textHeadLinesSouthAfrica = header.findViewById<Chip>(R.id.textHeadLinesSouthAfrica)
        val textHeadLinesUK = header.findViewById<Chip>(R.id.textHeadLinesUK)

        // for category
        val textAll = header.findViewById<Chip>(R.id.textAll)
        val textSport = header.findViewById<Chip>(R.id.textSport)
        val textEntertainment = header.findViewById<Chip>(R.id.textEntertainment)
        val textBussiness = header.findViewById<Chip>(R.id.textBussiness)
        val textHealth = header.findViewById<Chip>(R.id.textHealth)
        val textScience = header.findViewById<Chip>(R.id.textScience)
        val textTechnology = header.findViewById<Chip>(R.id.textTechnology)


        textHeadLinesUs.isChecked = this.getSelectedCountry() == "us"
        textHeadLinesNigeria.isChecked = this.getSelectedCountry() == "ng"
        textHeadLinesCanada.isChecked = this.getSelectedCountry() == "ca"
        textHeadLinesSouthAfrica.isChecked = this.getSelectedCountry() == "sa"
        textHeadLinesUK.isChecked = this.getSelectedCountry() == "gb"


        textAll.setOnClickListener {
            val intent = Intent(Constants.Gallery_Fragment)
            intent.putExtra(Constants.New_country, "")
            applicationContext.sendBroadcast(intent)
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        textSport.setOnClickListener {
            val intent = Intent(Constants.Gallery_Fragment)
            intent.putExtra(Constants.New_country, "sport")
            applicationContext.sendBroadcast(intent)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        textTechnology.setOnClickListener {
            val intent = Intent(Constants.Gallery_Fragment)
            intent.putExtra(Constants.New_country, "technology")
            applicationContext.sendBroadcast(intent)
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        textScience.setOnClickListener {
            val intent = Intent(Constants.Gallery_Fragment)
            intent.putExtra("New_Category", "science")
            applicationContext.sendBroadcast(intent)
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        textBussiness.setOnClickListener {
            val intent = Intent(Constants.Gallery_Fragment)
            intent.putExtra(Constants.New_country, "business")
            applicationContext.sendBroadcast(intent)
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        textEntertainment.setOnClickListener {
            val intent = Intent(Constants.Gallery_Fragment)
            intent.putExtra(Constants.New_country, "entertainment")
            applicationContext.sendBroadcast(intent)
            drawerLayout.closeDrawer(GravityCompat.START)
        }


        textHealth.setOnClickListener {
            val intent = Intent(Constants.Gallery_Fragment)
            intent.putExtra(Constants.New_country, "health")
            applicationContext.sendBroadcast(intent)
            drawerLayout.closeDrawer(GravityCompat.START)
        }


        // for the countries

        textHeadLinesSouthAfrica.setOnClickListener {
            saveSelectedCountry("sa")
            val intent = Intent(Constants.Gallery_Fragment)
            intent.putExtra(Constants.New_country, "sa")
            applicationContext.sendBroadcast(intent)
            drawerLayout.closeDrawer(GravityCompat.START)
        }


        textHeadLinesCanada.setOnClickListener {
            saveSelectedCountry("ca")

            val intent = Intent(Constants.Gallery_Fragment)
            intent.putExtra(Constants.New_country, "ca")
            applicationContext.sendBroadcast(intent)
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        textHeadLinesUs.setOnClickListener {
            saveSelectedCountry("us")
            val intent = Intent(Constants.Gallery_Fragment)
            intent.putExtra(Constants.New_country, "us")
            applicationContext.sendBroadcast(intent)
            drawerLayout.closeDrawer(GravityCompat.START)
        }



        textHeadLinesNigeria.setOnClickListener {

            saveSelectedCountry("ng")

            val intent = Intent(Constants.Gallery_Fragment)
            intent.putExtra(Constants.New_country, "ng")
            applicationContext.sendBroadcast(intent)
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        textHeadLinesUK.setOnClickListener {

            saveSelectedCountry("gb")

            val intent = Intent(Constants.Gallery_Fragment)
            intent.putExtra(Constants.New_country, "gb")
            applicationContext.sendBroadcast(intent)
            drawerLayout.closeDrawer(GravityCompat.START)
        }


        settings.setOnClickListener {

            drawerLayout.closeDrawer(GravityCompat.START)

            handler.postDelayed(Runnable {
                if (navController.currentDestination?.id != R.id.savedFragment) {
                    navController.navigate(R.id.action_main_Home_Fragment_to_setting_theme_Fragment)
                }
            }, 300)
        }


    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private class MyBroadcastReceiver(private val drawerLayoutRef: WeakReference<DrawerLayout>) :
        BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            drawerLayoutRef.get()?.openDrawer(GravityCompat.START)

        }
    }

    override fun onResume() {
        super.onResume()
        try {
            val intentFilter = IntentFilter(Constants.MainActivity)
            registerReceiver(receiver, intentFilter)

            val editor = shared_time_count.edit()
            editor.putString("STATE_APP", "RUNNING")
            editor.apply()

        } catch (_: Exception) {
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(receiver)
        } catch (_: Exception) {
        }
    }

    private fun applyTheme(theme: Int) {
        when (theme) {
            AppSettings.THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            handler.removeCallbacksAndMessages(null)
            val editor = shared_time_count.edit()
            editor.putString("STATE_APP", "BACKGROUND")
            editor.remove("onLoad_System")
            editor.apply()


        } catch (_: Exception) {
        }


    }


    private fun saveSelectedCountry(countryCode: String) {
        val sharedPref =
            getSharedPreferences(Constants.search_Query_Shared_Prefs, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("selected_country", countryCode)
        editor.apply()
    }


    private fun getSelectedCountry(): String {
        val sharedPref =
            getSharedPreferences(Constants.search_Query_Shared_Prefs, Context.MODE_PRIVATE)
        return sharedPref.getString("selected_country", "us") ?: "us"
    }


    fun app_update_statues() {

        try {
            @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("MMMM d, yyyy")
            val calendar = Calendar.getInstance()
            val currentDate = calendar.time
            val doNotShowAgain = shared_do_no_show.getBoolean(Constants.DO_NO_SHOW_AGAIN, false)
            var app_date: Date?


            appUpdateViewModel.appUpdateLiveData.observe(this, Observer { response ->
                response?.let {

                    val my_app_date = response.date_publish
                    app_date = dateFormat.parse(my_app_date + "")

                    if (response.version == null && !doNotShowAgain) {
                        showPopup("" + response.date_publish, "" + response.url)
                    } else if (currentDate.compareTo(app_date) > 0) {

                        final_showPopup("" + response.date_publish, "" + response.url)

                    }

                }

            })

            appUpdateViewModel.checkAppUpdate()

        } catch (_: Exception) {
        }

    }


    @SuppressLint("SetTextI18n")
    fun showPopup(date_publish: String, url: String) {
        try {
            val popupView = layoutInflater.inflate(R.layout.popup_layout, null)
            val doNotShowAgainCheckBox = popupView.findViewById<CheckBox>(R.id.checkBox)
            val textUpdatedescription = popupView.findViewById<TextView>(R.id.textUpdatedescription)
            textUpdatedescription.text = "An app update will be required before   $date_publish"
            val builder = AlertDialog.Builder(this)
            builder.setView(popupView)
            builder.setPositiveButton(
                "Proceed"
            ) { dialogInterface, _ ->
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("" + url)
                    )
                )
            }
            builder.setNegativeButton(
                "Dismiss"
            ) { _, i ->
                val doNotShowAgain = doNotShowAgainCheckBox.isChecked
                shared_do_no_show.edit().putBoolean(Constants.DO_NO_SHOW_AGAIN, doNotShowAgain)
                    .apply()

            }
            val dialog = builder.create()
            dialog.show()
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window!!.attributes)
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window!!.attributes = layoutParams
        } catch (ignored: java.lang.Exception) {
        }
    }


    @SuppressLint("SetTextI18n")
    fun final_showPopup(date_publish: String, url: String) {
        try {
            val popupView = layoutInflater.inflate(R.layout.popup_layout_final, null)
            val textUpdatedescription = popupView.findViewById<TextView>(R.id.textUpdatedescription)
            textUpdatedescription.text =
                "This current version was obsolete on:  $date_publish    Kindly Download the Latest Version"
            val builder = AlertDialog.Builder(this)
            builder.setView(popupView)
            builder.setCancelable(false)
            builder.setPositiveButton(
                "Proceed"
            ) { dialogInterface, i ->
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("" + url)
                    )
                )
            }
            builder.setNegativeButton(
                "Close "
            ) { dialogInterface, i -> finish() }
            val dialog = builder.create()
            dialog.show()
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window!!.attributes)
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window!!.attributes = layoutParams
        } catch (ignored: java.lang.Exception) {
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            val editor = shared_time_count.edit()
            editor.putString("STATE_APP", "BACKGROUND")
            editor.apply()
        } catch (_: Exception) { }
    }

    private fun checkingForPendingIntent() {
        try {
            handler.postDelayed(Runnable {
                val jamesValue = intent.getStringExtra("James")
                val getTitle = "" + shared_time_count.getString("NewsTitle", "")
                val getUrl = "" + shared_time_count.getString("getUrl", "")
                val urlToImage = "" + shared_time_count.getString("urlToImage", "")
                val author = "" + shared_time_count.getString("author", "")
                val publishedAt = "" + shared_time_count.getString("publishedAt", "")

                if (jamesValue != null) {
                    val intent22 = Intent(Constants.Main_Home_Fragment)
                    intent22.putExtra("Navigation", Constants.Navigate_To_Detail_Fragment)
                    applicationContext.sendBroadcast(intent22)
                    sharedPrefPassDataToDetailsFragment(getTitle, getUrl, urlToImage, author, publishedAt)
                    saveUserProfilesAndNewsArticleId(publishedAt, Constants.My_Main_Home_Fragment)

                }
            },300)

        }catch (_:Exception){}


    }




    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        try {

            if (intent != null) {
                val jamesValue = intent.getStringExtra("James")

                val getTitle = "" + shared_time_count.getString("NewsTitle", "")
                val getUrl = "" + shared_time_count.getString("getUrl", "")
                val urlToImage = "" + shared_time_count.getString("urlToImage", "")
                val author = "" + shared_time_count.getString("author", "")
                val publishedAt = "" + shared_time_count.getString("publishedAt", "")

                if (jamesValue != null) {
                    val intent22 = Intent(Constants.Main_Home_Fragment)
                    intent22.putExtra("Navigation", Constants.Navigate_To_Detail_Fragment)
                    applicationContext.sendBroadcast(intent22)
                    sharedPrefPassDataToDetailsFragment(getTitle, getUrl, urlToImage, author, publishedAt)
                    saveUserProfilesAndNewsArticleId(publishedAt, Constants.My_Main_Home_Fragment)
                }
            }
        }catch (_:Exception){}
    }


    @SuppressLint("CommitPrefEdits")
    fun sharedPrefPassDataToDetailsFragment(titles:String, getUrl:String, urlToImage:String, name:String, date:String){
        val editor = sharedPrefPassDataToDetailsFragment.edit()
        editor.putString("titles", titles)
        editor.putString("getUrl", getUrl)
        editor.putString("urlToImage", urlToImage)
        editor.putString("name", name)
        editor.putString("date", date)
        editor.apply()



    }

    private fun saveUserProfilesAndNewsArticleId(newsArticleId: String, whatFragment: String) {
        val  editor = sharedPref.edit()
        editor.putString("newsArticleId", newsArticleId)
        editor.putString("WhatFragment", whatFragment)
        editor.apply()
    }


}

