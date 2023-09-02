package com.example.vectonews.ui.detailfragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.vectonews.R
import com.example.vectonews.api.Source
import com.example.vectonews.api.UnsplashPhoto
import com.example.vectonews.comments.CommentFragment
import com.example.vectonews.databinding.FragmentDetailBinding
import com.example.vectonews.offlinecenter.SavedViewModel
import com.example.vectonews.settings.AppSettings
import com.example.vectonews.util.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!


    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }
    private val settings: AppSettings by lazy {
        AppSettings(requireContext().applicationContext)
    }


    private val sharedDass: SharedPreferences by lazy {
        requireContext().applicationContext.getSharedPreferences(
            Constants.USER_PROFILE,
            Context.MODE_PRIVATE
        )
    }

    private val savedViewModel by viewModels<SavedViewModel>()


    private val sharedPrefPassDataToDetailsFragment: SharedPreferences by lazy {
        requireContext().getSharedPreferences(
            Constants.PASS_DATA_TO_DETAIL_FRAGMENT,
            Context.MODE_PRIVATE
        )

    }


    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailBinding.bind(view)


        changeToolabrColor()
        getCommentCounts()
        CheckIsBookMarked()
        operation_Delete_and_save()


        binding.apply {

            btnBackPressed.setOnClickListener {

                val WhatFragment = sharedDass.getString("WhatFragment", "")
                if (WhatFragment.equals("My_Main_Home_Fragment")) {
                    findNavController().popBackStack(R.id.main_Home_Fragment, false)
                } else {
                    findNavController().popBackStack(R.id.searchFragment, false)
                }
            }



            textViewComments.setOnClickListener {
                textViewComments.isEnabled = false
                val customPopupFragment = CommentFragment()
                customPopupFragment.show(childFragmentManager, customPopupFragment.tag)

                handler.postDelayed(Runnable { textViewComments.isEnabled = true }, 200)

            }


            val titles = sharedPrefPassDataToDetailsFragment.getString("titles", "")


            if (titles.toString().isNotEmpty()) {
                textView3.text = titles
            }




            webView.webViewClient = WebViewClient()

            webView.settings.javaScriptEnabled = true
            webView.settings.setSupportZoom(true)
            webView.settings.builtInZoomControls = true

            webView.settings.displayZoomControls = false
            webView.settings.useWideViewPort = true
            webView.settings.loadWithOverviewMode = true



            if (savedInstanceState != null) {
                webView.restoreState(savedInstanceState)
            } else {
                val getUrl = sharedPrefPassDataToDetailsFragment.getString("getUrl", "")


                webView.loadUrl("" + getUrl)
            }

            swipeMotionLayout.setOnRefreshListener {
                webView.reload()
            }



            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    progressBar.visibility =
                        View.VISIBLE // Show ProgressBar when the page starts loading
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    progressBar.visibility = View.GONE
                    swipeMotionLayout.isRefreshing = false
                }

            }

        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }


    override fun onResume() {
        super.onResume()
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.apply {

                    val WhatFragment = sharedDass.getString("WhatFragment", "")

                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else if (WhatFragment.equals("My_Main_Home_Fragment")) {
                        findNavController().popBackStack(R.id.main_Home_Fragment, false)
                    } else {
                        findNavController().popBackStack(R.id.searchFragment, false)
                    }
                }

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
    }


    @SuppressLint("ObsoleteSdkInt")
    private fun changeToolabrColor() {

        try {
            if (settings.getTheme() == AppSettings.THEME_LIGHT) {
                activity?.window?.statusBarColor = Color.parseColor("#E8EDFA")

            } else {
                activity?.window?.statusBarColor = Color.parseColor("#2E2E2E")

            }
        } catch (_: Exception) {
        }


    }


    fun getCommentCounts() {

        try {
            val database = Firebase.database
            val newsFeedReference = database.getReference("AllCommentsArticles")
            val newsArticleId = sharedDass.getString("newsArticleId", "")!!

            newsFeedReference.child(newsArticleId)
                .addValueEventListener(object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.exists()) {
                            try {
                                val counts = snapshot.childrenCount
                                binding.textViewComments.text = "View ${counts.toString()} Comments"
                            } catch (_: Exception) {
                            }
                        } else {
                            try {
                                binding.textViewComments.text = "Let talk about it?"
                            } catch (_: Exception) {
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Nothing to do
                    }
                })


        } catch (_: Exception) {
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        try {
            _binding = null

            handler.removeCallbacksAndMessages { null }
        } catch (_: Exception) {
        }
    }


    fun CheckIsBookMarked() {

        val titles = sharedPrefPassDataToDetailsFragment.getString("titles", "")
        savedViewModel.allNotes.observe(viewLifecycleOwner, Observer {
            val isSaved = it.any { note -> note.title == "" + titles }
            updateBookmarkIcon(isSaved)
        })

    }

    fun updateBookmarkIcon(isSaved: Boolean) {
        if (isSaved) {

            try {
                binding.imageViewSelected.visibility = View.VISIBLE
                binding.imageViewUnselcted.visibility = View.INVISIBLE

            } catch (_: Exception) {
            }
        } else {
            try {

                binding.imageViewSelected.visibility = View.INVISIBLE
                binding.imageViewUnselcted.visibility = View.VISIBLE

            } catch (_: Exception) {
            }

        }
    }


    fun operation_Delete_and_save() {

        binding.apply {

            binding.imageViewSelected.setOnClickListener {

                try {
                    val titles = sharedPrefPassDataToDetailsFragment.getString("titles", "")
                    val getUrl = sharedPrefPassDataToDetailsFragment.getString("getUrl", "")
                    val urlToImage = sharedPrefPassDataToDetailsFragment.getString("urlToImage", "")
                    val date = sharedPrefPassDataToDetailsFragment.getString("date", "")
                    val name = sharedPrefPassDataToDetailsFragment.getString("name", "")

                    val _userName = Source("", "" + name)
                    val artciles =
                        UnsplashPhoto("" + titles, "" + getUrl , "" + urlToImage, _userName,"" + date)
                    artciles.isSaved = false
                    deleteAllUsers(artciles)

                    binding.imageViewSelected.visibility = View.INVISIBLE
                    binding.imageViewUnselcted.visibility = View.VISIBLE
                } catch (_: Exception) {
                }
            }



            binding.imageViewUnselcted.setOnClickListener {
                try {
                    binding.imageViewSelected.visibility = View.VISIBLE
                    binding.imageViewUnselcted.visibility = View.INVISIBLE

                    val title = sharedPrefPassDataToDetailsFragment.getString("titles", "")
                    val getUrl = sharedPrefPassDataToDetailsFragment.getString("getUrl", "")
                    val urlToImage = sharedPrefPassDataToDetailsFragment.getString("urlToImage", "")
                    val date = sharedPrefPassDataToDetailsFragment.getString("date", "")
                    val name = sharedPrefPassDataToDetailsFragment.getString("name", "")

                    val _userName = Source("", "" +name)
                    val artciles =
                        UnsplashPhoto("" + title, "" + getUrl , "" + urlToImage, _userName,"" + date)

                    artciles.isSaved = true
                    savedViewModel.insert(artciles)
                } catch (_: Exception) {
                }

            }


        }
    }


    private fun deleteAllUsers(artciles: UnsplashPhoto) {

        try {
            val builder = AlertDialog.Builder(requireContext())
            builder.setPositiveButton("Yes") { _, _ ->
                savedViewModel.delete(artciles)
                Toast.makeText(
                    requireContext(),
                    "Article deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.setTitle("Delete News Article?")
            builder.create().show()
        } catch (_: Exception) {
        }
    }


}