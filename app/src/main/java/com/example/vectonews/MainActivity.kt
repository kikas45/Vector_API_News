package com.example.vectonews

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.vectonews.settings.AppSettings
import com.google.android.material.navigation.NavigationView
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var receiver: MyBroadcastReceiver

    private lateinit var navController: NavController

    private val settings: AppSettings by lazy {
        AppSettings(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val imageViewer = header.findViewById<ImageView>(R.id.imageViewer)
        val settings = header.findViewById<TextView>(R.id.settings)
        val textProfile = header.findViewById<TextView>(R.id.textProfile)

        imageViewer.setOnClickListener {
            Toast.makeText(applicationContext, "Yes Header", Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        textProfile.setOnClickListener {
            Toast.makeText(applicationContext, "Yes Header", Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        settings.setOnClickListener {

            drawerLayout.closeDrawer(GravityCompat.START)
            if (navController.currentDestination?.id != R.id.savedFragment) {
                navController.navigate(R.id.action_main_Home_Fragment_to_setting_theme_Fragment)
            }
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


}