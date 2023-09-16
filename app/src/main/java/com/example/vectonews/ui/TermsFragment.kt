package com.example.vectonews.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.vectonews.R
import com.example.vectonews.databinding.FragmentTermsBinding
import com.example.vectonews.settings.AppSettings
import com.example.vectonews.util.Constants


class TermsFragment : Fragment(R.layout.fragment_terms) {
    private var _binding: FragmentTermsBinding? = null
    private val binding get() = _binding!!

    private val settings: AppSettings by lazy {
        AppSettings(requireContext().applicationContext)
    }


    private val sharedDass: SharedPreferences by lazy {
        requireContext().applicationContext.getSharedPreferences(
            Constants. terms_and_conditions,
            Context.MODE_PRIVATE
        )
    }

    // working for the shared preference of notification
    private val shared_time_count: SharedPreferences by lazy {
        requireContext().getSharedPreferences(
            Constants.SHARED_TIME,
            Context.MODE_PRIVATE
        )
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTermsBinding.bind(view)


        binding.apply {
            button.setOnClickListener {

                if (checkBox.isChecked){
                    val doNotShowAgain = checkBox.isChecked
                    sharedDass.edit().putBoolean("The_box_Checked", doNotShowAgain).apply()

                    // enable notification right away
                    val editor = shared_time_count.edit()
                    editor.putString(Constants.isActive, "Button_Active")
                    editor.apply()

                    findNavController().navigate(R.id.action_termsFragment_to_main_Home_Fragment2)

                }
            }

        }


        changeToolbarColor()


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


    override fun onStart() {
        super.onStart()
        val doNotShowAgain = sharedDass.getBoolean("The_box_Checked", false)

        if (doNotShowAgain){
            findNavController().navigate(R.id.action_termsFragment_to_main_Home_Fragment2)
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}