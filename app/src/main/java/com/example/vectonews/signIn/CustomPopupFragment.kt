package com.example.vectonews.signIn

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.example.vectonews.R
import com.example.vectonews.databinding.FragmentCustomPopupBinding
import com.example.vectonews.util.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomPopupFragment : BottomSheetDialogFragment(R.layout.fragment_custom_popup) {


    private var _binding: FragmentCustomPopupBinding? = null
    private val binding get() = _binding!!


    private val sharedPref: SharedPreferences by lazy {
        requireContext().getSharedPreferences(Constants.USER_PROFILE, Context.MODE_PRIVATE)

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCustomPopupBinding.bind(view)


        val userName = sharedPref.getString("userName", "")


        binding.apply {

            if (userName.isNullOrEmpty()) {
                textuserName.text = "Please wait..."
            } else {
                textuserName.text =  "Hi, " + userName.toString()
            }

            btnSigingIn.setOnClickListener {

                val intent = Intent("Main_Home_Fragment")
                intent.putExtra("Navigation", "signWithGoogle")
                requireContext().sendBroadcast(intent)

                dismiss()
            }

            btnsignOut.setOnClickListener {

                val intent = Intent("Main_Home_Fragment")
                intent.putExtra("Navigation", "signOutWithGoogle")
                requireContext().sendBroadcast(intent)

                dismiss()
            }
        }

    }


    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        binding.apply {
            if (GoogleSignIn.getLastSignedInAccount(requireContext()) != null) {
                btnSigingIn.visibility = View.INVISIBLE
                btnsignOut.visibility = View.VISIBLE
                textuserName.visibility = View.VISIBLE
            } else {
                btnSigingIn.visibility = View.VISIBLE
                btnsignOut.visibility = View.INVISIBLE
                textuserName.text = "Sign in with Google"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}