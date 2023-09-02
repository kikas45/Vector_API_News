package com.example.vectonews.comments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.vectonews.R
import com.example.vectonews.databinding.FragmentBSCommentBinding
import com.example.vectonews.databinding.FragmentCommentBinding
import com.example.vectonews.util.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BSCommentFragment : BottomSheetDialogFragment(R.layout.fragment_b_s_comment) {

    private val sharedDass: SharedPreferences by lazy {
        requireContext().applicationContext.getSharedPreferences(
            Constants.USER_PROFILE,
            Context.MODE_PRIVATE
        )
    }

    private val passCommentsToDetailFragment: SharedPreferences by lazy {
        requireContext().applicationContext.getSharedPreferences(
            Constants.passCommentsToDetailFragment,
            Context.MODE_PRIVATE
        )
    }



    private var _binding: FragmentBSCommentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBSCommentBinding.bind(view)


        binding.apply {

            textReport.setOnClickListener {
                val intent = Intent("CommentFragmentGoogleSign")
                intent.putExtra("typeOfBottomSheet", "showSnackbar_Flaged_Report")
                requireContext().sendBroadcast(intent)
                dismiss()

            }



            val commentUserId = passCommentsToDetailFragment.getString("userId", "")
            val currentUserid = sharedDass.getString("userId", "")

            if (commentUserId == currentUserid) {

                try {
                    textOpenComment.visibility = View.VISIBLE
                    textDelete.visibility = View.VISIBLE

                    textDelete.setOnClickListener {
                        val intent = Intent("CommentFragmentGoogleSign")
                        intent.putExtra("typeOfBottomSheet", "DeleteComment_By_BSc_Order")
                        requireContext().sendBroadcast(intent)
                        dismiss()

                    }

                    textOpenComment.setOnClickListener {
                        val intent = Intent("CommentFragmentGoogleSign")
                        intent.putExtra("typeOfBottomSheet", "OpenComment_By_BSc_Order")
                        requireContext().sendBroadcast(intent)
                        dismiss()
                    }



                    }catch (_:Exception){}

            } else {

                try {
                    textOpenComment.visibility = View.VISIBLE
                    textDelete.visibility = View.GONE

                    textOpenComment.setOnClickListener {
                    val intent = Intent("CommentFragmentGoogleSign")
                    intent.putExtra("typeOfBottomSheet", "OpenComment_By_BSc_Order")
                    requireContext().sendBroadcast(intent)
                    dismiss()

                }

                }catch (_:Exception){}
            }


        }


    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            _binding = null
        }catch (_:Exception){}
    }
}