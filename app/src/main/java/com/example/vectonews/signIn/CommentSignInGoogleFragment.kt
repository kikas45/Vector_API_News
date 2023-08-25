package com.example.vectonews.signIn

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.example.vectonews.R
import com.example.vectonews.databinding.FragmentCommentGoogleSignBinding
import com.example.vectonews.util.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CommentSignInGoogleFragment : BottomSheetDialogFragment(R.layout.fragment_comment_google_sign) {


    private var _binding: FragmentCommentGoogleSignBinding? = null
    private val binding get() = _binding!!

    private val sharedPref: SharedPreferences by lazy {
        requireContext().getSharedPreferences(Constants.USER_PROFILE, Context.MODE_PRIVATE)

    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCommentGoogleSignBinding.bind(view)

        val userName = sharedPref.getString("userName", "")


        binding.apply {

            btnSigingIn.setOnClickListener {
                val intent = Intent("CommentFragmentGoogleSign")
                requireContext().sendBroadcast(intent)
                dismiss()
            }

            if (userName.isNullOrEmpty()) {
                textuserName.text = "Please wait..."
                btnsignOut.isEnabled = false
            } else {
                textuserName.text =  "Hi, $userName.toString()"
                btnsignOut.isEnabled = true
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
                textuserName.text = "Continue with Google"
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}