package com.example.vectonews.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vectonews.R
import com.example.vectonews.databinding.FragmentSettingThemeBinding


class Setting_theme_Fragment : Fragment(R.layout.fragment_setting_theme) {

    private var _binding: FragmentSettingThemeBinding? = null
    private val binding get() = _binding!!

    private val settings: AppSettings by lazy {
        AppSettings(requireContext().applicationContext)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingThemeBinding.bind(view)


        binding.closeBs.setOnClickListener {
            findNavController().popBackStack(R.id.main_Home_Fragment, false)
        }


        initView()
    }

    private fun initView() {
        when (settings.getTheme()) {
            AppSettings.THEME_LIGHT -> binding.rgTheme.isChecked = false
            AppSettings.THEME_DARK -> binding.rgTheme.isChecked = true
            else -> binding.rgTheme.isChecked = false
        }
        binding.rgTheme.setOnCheckedChangeListener { compoundButton, isValued -> // we are putting the values into SHARED PREFERENCE
            // this avlues are int 0 or 1
            if (compoundButton.isChecked) {
                settings.setTheme(AppSettings.THEME_DARK)
            } else {
                settings.setTheme(AppSettings.THEME_LIGHT)
            }


            /// we check here if any valued is ticked so we can save and recreate
            if (isValued) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                requireActivity().recreate()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                requireActivity().recreate()
            }
        }
    }

}