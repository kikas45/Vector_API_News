package com.example.vectonews.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.vectonews.R
import com.example.vectonews.databinding.FragmentMainHomeBinding
import com.example.vectonews.settings.AppSettings
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Main_Home_Fragment : Fragment(R.layout.fragment_main_home) {
    private lateinit var navController: NavController
    private val navigationBroadcastReceiver = NavigationBroadcastReceiver()

    private var _binding: FragmentMainHomeBinding? = null
    private val binding get() = _binding!!

    private val settings: AppSettings by lazy {
        AppSettings(requireContext().applicationContext)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainHomeBinding.bind(view)

        val filter = IntentFilter("Main_Home_Fragment")
        requireContext().registerReceiver(navigationBroadcastReceiver, filter)



        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragmentChildContainer) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomChildNavigationView)
        bottomNavigationView.setupWithNavController(navController)



        changeToolbarColor()

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

        try {
            requireContext().unregisterReceiver(navigationBroadcastReceiver)
        }catch (io:Exception){}
    }


    @SuppressLint("ObsoleteSdkInt")
    private fun changeToolbarColor() {

        if (settings.getTheme() == AppSettings.THEME_LIGHT){
            activity?.window?.statusBarColor = Color.parseColor("#FFFFFF")

        }else{
            activity?.window?.statusBarColor = Color.parseColor("#1E1D1D")

        }

    }

}