package com.example.vectonews

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var receiver: MyBroadcastReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()


        drawerLayout = findViewById(R.id.drawer_layout)

        val drawerLayoutRef = WeakReference(drawerLayout)
        receiver = MyBroadcastReceiver(drawerLayoutRef)



        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_item1 -> {

                    Toast.makeText(applicationContext, "Yes ", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawer(GravityCompat.START)

                    //   if (navController.currentDestination?.id != R.id.savedFragment) {
                    //  navController.navigate(R.id.action_homeFragment_to_savedFragment)
                    //   }

                    true
                }

                R.id.nav_item2 -> {
                    // Handle item 2 click
                    Toast.makeText(applicationContext, "Yes ", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.nav_item3 -> {
                    Toast.makeText(applicationContext, "Yes ", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                else -> false
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

    class MyBroadcastReceiver(private val drawerLayoutRef: WeakReference<DrawerLayout>) :
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

}