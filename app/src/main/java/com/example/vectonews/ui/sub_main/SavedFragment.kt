package com.example.vectonews.ui.sub_main


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.vectonews.R
import com.example.vectonews.api.UnsplashPhoto
import com.example.vectonews.comments.CommentFragment
import com.example.vectonews.databinding.FragmentSavedBinding
import com.example.vectonews.offlinecenter.SavedDetailAdapter
import com.example.vectonews.offlinecenter.SavedViewModel
import com.example.vectonews.settings.AppSettings
import com.example.vectonews.signIn.CustomPopupFragment
import com.example.vectonews.util.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedFragment : Fragment(R.layout.fragment_saved),
    SavedDetailAdapter.OnItemClickListenerDetails, SavedDetailAdapter.OnItemLongClickListenerSaved,
    SavedDetailAdapter.OnBsClickItemOnSaved {

    private val mUserViewModel by viewModels<SavedViewModel>()

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    private val sharedPref: SharedPreferences by lazy {
        requireContext().getSharedPreferences(Constants.USER_PROFILE, Context.MODE_PRIVATE)

    }

    private lateinit var userId: String

    private lateinit var editor: SharedPreferences.Editor


    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }


    private val adapter: SavedDetailAdapter by lazy {
        SavedDetailAdapter(this, this, this)
    }

    private val sharedHandleSearchNavigation: SharedPreferences by lazy {
        requireContext().getSharedPreferences(Constants.handleSearchNavigation, Context.MODE_PRIVATE)

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSavedBinding.bind(view)


        binding.apply {


            try {

                hamburger.setOnClickListener {
                    val intent = Intent("MainActivity")
                    requireContext().sendBroadcast(intent)
                }


                val sharedPref =
                    requireContext().getSharedPreferences(Constants.USER_PROFILE, Context.MODE_PRIVATE)
                val image = sharedPref.getString("userImage", "")
                userId = sharedPref.getString("userId", "")!!

                Glide.with(requireContext())
                    .load(image)
                    .centerCrop()
                    .error(R.drawable.ic_launcher_background)
                    .into(profileImage)



                profileImage.setOnClickListener {
                    val customPopupFragment = CustomPopupFragment()
                    customPopupFragment.show(childFragmentManager, customPopupFragment.tag)
                }




                textsearch.setOnClickListener {
                    val intent = Intent("Main_Home_Fragment")
                    intent.putExtra("Navigation", "Navigate_to_SearchHistoryFragment")
                    requireContext().sendBroadcast(intent)

                    handleSearchNavigation("Major_Home_Fragment")

                }


                swipeMotionFragment.setOnRefreshListener {
                    fectdatafromRoomBase()
                    swipeMotionFragment.isRefreshing = false
                }


                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())

                binding.progressBar.visibility = View.VISIBLE
                recyclerView.visibility = View.INVISIBLE
                handler.postDelayed(Runnable {
                    binding.progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }, 1000)

            } catch (_: Exception) {
            }


        }


        fectdatafromRoomBase()


    }

    private fun fectdatafromRoomBase() {
        mUserViewModel.allNotes.observe(viewLifecycleOwner, Observer {

            if (it.isEmpty()) {
                binding.textViewEmpty.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
            } else {
                binding.textViewEmpty.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                adapter.setData(it)
            }


        })


    }


    override fun onResume() {
        super.onResume()
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view?.findNavController()?.popBackStack(R.id.gallaryFragment, false)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
    }


    //for details Fragments
    override fun onclickDetailsItem(photo: UnsplashPhoto) {

        val getUrl = photo.url.toString()
        val titles = photo.title.toString()

        val artcileId = photo.publishedAt.toString()
        saveUserProfilesAndNewsArticleId(artcileId, "My_Main_Home_Fragment")

        val intent = Intent("Main_Home_Fragment")
        intent.putExtra("Navigation", "Navigate_To_Detail_Fragment")
        intent.putExtra("titles", titles)
        intent.putExtra("getUrl", getUrl)
        requireContext().sendBroadcast(intent)




    }

    override fun onItemLongClickedSaved(photo: UnsplashPhoto) {
        deleteAllUsers(photo)
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }

    private fun deleteAllUsers(photo: UnsplashPhoto) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mUserViewModel.delete(photo)
            Toast.makeText(
                requireContext(),
                "Successfully removed everything",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete News Article?")
        builder.create().show()
    }

    override fun onItemBsSaved(photo: UnsplashPhoto) {
        val artcileId = photo.publishedAt.toString()
        saveUserProfilesAndNewsArticleId(artcileId, "My_Main_Home_Fragment")
        val customPopupFragment = CommentFragment()
        customPopupFragment.show(childFragmentManager, customPopupFragment.tag)


    }

    private fun saveUserProfilesAndNewsArticleId(newsArticleId: String, whatFragment:String) {

        editor = sharedPref.edit()
        editor.putString("newsArticleId", newsArticleId)
        editor.putString("WhatFragment", whatFragment)
        editor.apply()
    }

    private fun handleSearchNavigation(handleSearchNavigation:String) {
        editor = sharedHandleSearchNavigation.edit()
        editor.putString("handleSearchNavigation", handleSearchNavigation)
        editor.apply()
    }


}

