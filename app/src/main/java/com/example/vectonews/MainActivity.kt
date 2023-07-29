package com.example.vectonews

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.vectonews.settings.AppSettings
import com.google.android.material.chip.Chip
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var receiver: MyBroadcastReceiver

    private lateinit var navController: NavController

    private val settings: AppSettings by lazy {
        AppSettings(applicationContext)
    }



    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        //   Thread.sleep(2000)  /// remember to remove this after production
        applyTheme(settings.getTheme())
        setContentView(R.layout.activity_main)
        setTheme(R.style.Base_Theme_VectoNews)
        supportActionBar?.hide()

        //note theme settings must be called before the setContentView and onCreate


        drawerLayout = findViewById(R.id.drawer_layout)

        val drawerLayoutRef = WeakReference(drawerLayout)
        receiver = MyBroadcastReceiver(drawerLayoutRef)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController


        val navigationView: NavigationView = findViewById(R.id.navigation_view)

        // inflate the drawer head
        val header = navigationView.inflateHeaderView(R.layout.drawer_header)

        val settings = header.findViewById<TextView>(R.id.settings)

        val textHeadLinesUs = header.findViewById<Chip>(R.id.textHeadLinesUs)
        val textHeadLinesNigeria = header.findViewById<Chip>(R.id.textHeadLinesNigeria)
        val textHeadLinesCanada = header.findViewById<TextView>(R.id.textHeadLinesCanada)
        val textHeadLinesSouthAfrica = header.findViewById<Chip>(R.id.textHeadLinesSouthAfrica)




        textHeadLinesSouthAfrica.setOnClickListener {
            saveSelectedCountry("sa")
            val intent = Intent("Gallery_Fragment")
            intent.putExtra("New_country", "sa")
            applicationContext.sendBroadcast(intent)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        textHeadLinesCanada.setOnClickListener {
            saveSelectedCountry("ca")

            val intent = Intent("Gallery_Fragment")
            intent.putExtra("New_country", "ca")
            applicationContext.sendBroadcast(intent)
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        textHeadLinesUs.setOnClickListener {
            saveSelectedCountry("us")
            val intent = Intent("Gallery_Fragment")
            intent.putExtra("New_country", "us")
            applicationContext.sendBroadcast(intent)
            drawerLayout.closeDrawer(GravityCompat.START)
        }


        textHeadLinesNigeria.setOnClickListener {

            saveSelectedCountry("ng")
            val intent = Intent("Gallery_Fragment")
            intent.putExtra("New_country", "ng")
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
        val intentFilter = IntentFilter("MainActivity")
        registerReceiver(receiver, intentFilter)

    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
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
        } catch (_: Exception) {
        }


    }


    private fun saveSelectedCountry(countryCode: String) {
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("selected_country", countryCode)
        editor.apply()
    }


    private fun getSelectedCountry(): String {
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("selected_country", "us") ?: "us"
    }



}