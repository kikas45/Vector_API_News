package com.example.vectonews

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import java.util.prefs.NodeChangeListener

class MainActivity2 : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var listener: NavController.OnDestinationChangedListener

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        drawerLayout = findViewById(R.id.drawer_layout)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentNavCont) as NavHostFragment
        navController = navHostFragment.findNavController()

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        val navigationView = findViewById<NavigationView>(R.id.navigation_view22)
        navigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)


        listener = NavController.OnDestinationChangedListener{controller, destination, arguments ->

            if (destination.id == R.id.small_Two){
                supportActionBar?.setBackgroundDrawable(ColorDrawable(getColor(R.color.black_shade_1)))
                Toast.makeText(applicationContext, "yes", Toast.LENGTH_SHORT).show()


            }else if (destination.id == R.id.small_One){
                supportActionBar?.setBackgroundDrawable(ColorDrawable(getColor(R.color.black_shade_2)))
                Toast.makeText(applicationContext, "no", Toast.LENGTH_SHORT).show()

            }
        }


    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentNavCont)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listener)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(listener)
    }
}