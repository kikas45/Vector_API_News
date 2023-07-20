package com.example.vectonews.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.registerReceiver
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.vectonews.MainActivity
import com.example.vectonews.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class Main_Home_Fragment : Fragment(R.layout.fragment_main_home) {
    private lateinit var navController: NavController

    private val navigationBroadcastReceiver = NavigationBroadcastReceiver()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filter = IntentFilter("Main_Home_Fragment")
        requireContext().registerReceiver(navigationBroadcastReceiver, filter)



        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragmentChildContainer) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomChildNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        // Set up the hamburger icon click listener
        val hamburgerIcon = view.findViewById<ImageView>(R.id.backArrowPre)
        hamburgerIcon.setOnClickListener {

            val intent = Intent("MainActivity")
            view.context.sendBroadcast(intent)

        }


        val textview = view.findViewById<TextView>(R.id.titleText)

        textview.setOnClickListener {
            findNavController().navigate(R.id.action_main_Home_Fragment_to_main_Save_Fragment)
        }

        changeToolbarColor(view)

    }




    // receiving navigation command from Home and Save fragment
    class NavigationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Handle the broadcast action here and navigate to Main_Save_Fragment
            val navController = getNavController(context)
            navController.navigate(R.id.action_main_Home_Fragment_to_main_Save_Fragment)
        }

        private fun getNavController(context: Context): NavController {
            // accessing the container fragment in Main activity
            val navHostFragment = (context as AppCompatActivity).supportFragmentManager
                .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            return navHostFragment.navController
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        requireContext().unregisterReceiver(navigationBroadcastReceiver)
    }


    @SuppressLint("ObsoleteSdkInt")
    private fun changeToolbarColor(view: View) {

        val myToolbar:ConstraintLayout  = view.findViewById(R.id.myToolbar)
        myToolbar.setBackgroundColor(resources.getColor(R.color.black_shade_2))


        val newStatusBarColor = Color.parseColor("#1C4966")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.statusBarColor = newStatusBarColor
        }

    }

}