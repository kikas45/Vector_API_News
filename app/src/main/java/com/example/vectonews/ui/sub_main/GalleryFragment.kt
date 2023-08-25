package com.example.vectonews.ui.sub_main

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vectonews.R
import com.example.vectonews.api.Source
import com.example.vectonews.api.UnsplashPhoto
import com.example.vectonews.comments.CommentFragment
import com.example.vectonews.databinding.FragmentGalleryBinding
import com.example.vectonews.offlinecenter.SavedViewModel
import com.example.vectonews.settings.AppSettings
import com.example.vectonews.signIn.CustomPopupFragment
import com.example.vectonews.util.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery), NewsAdapter.OnItemClickListenerMe,
    NewsAdapter.OnBsClickItem,
    NewsAdapter.OnShortClickedAddItem {


    lateinit var countdownTimer: CountDownTimer
    private var seconds = 3L

    private val viewModel by viewModels<GalleryViewModel>()
    private val mUserViewModel by viewModels<SavedViewModel>()


    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private lateinit var userId: String

    private val adapter: NewsAdapter by lazy {
        NewsAdapter(
            this, this, this, mUserViewModel, viewLifecycleOwner
        )
    }

    private val sharedDass: SharedPreferences by lazy {
        requireContext().applicationContext.getSharedPreferences(
            Constants.CHECK_IF_CHIP_VALUE_EXIST_OR_NOT,
            Context.MODE_PRIVATE
        )
    }

    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val sharedPref: SharedPreferences by lazy {
        requireContext().getSharedPreferences(Constants.USER_PROFILE, Context.MODE_PRIVATE)

    }


    private val sharedHandleSearchNavigation: SharedPreferences by lazy {
        requireContext().getSharedPreferences(
            Constants.handleSearchNavigation,
            Context.MODE_PRIVATE
        )

    }

    private lateinit var editor: SharedPreferences.Editor

    private val settings: AppSettings by lazy {
        AppSettings(requireContext())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentGalleryBinding.bind(view)

        val filter = IntentFilter("Gallery_Fragment")
        requireContext().registerReceiver(broadcastReceiver, filter)



        binding.apply {
            hamburger.setOnClickListener {
                val intent = Intent("MainActivity")
                requireContext().sendBroadcast(intent)
            }

            val sharedPref =
                requireContext().getSharedPreferences(Constants.USER_PROFILE, Context.MODE_PRIVATE)
            val image = sharedPref.getString("userImage", "")
            userId = sharedPref.getString("userId", "")!!


            profileImage.setOnClickListener {
                val customPopupFragment = CustomPopupFragment()
                customPopupFragment.show(childFragmentManager, customPopupFragment.tag)
            }




            Glide.with(requireContext())
                .load(image)
                .centerCrop()
                .error(R.drawable.ic_launcher_background)
                .into(profileImage)


            textsearch.setOnClickListener {
                val intent = Intent(Constants.Main_Home_Fragment)
                intent.putExtra("Navigation", "Navigate_to_SearchHistoryFragment")
                requireContext().sendBroadcast(intent)

                handleSearchNavigation("Major_Home_Fragment")

            }

        }


        val editor = sharedDass.edit()

        binding.apply {
            try {

                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.itemAnimator = null
                // retry for head and footer
                recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                    header = NewsLoadStateAdapter { adapter.retry() },
                    footer = NewsLoadStateAdapter { adapter.retry() }
                )
                //    adapter.observeSavedStatus(viewLifecycleOwner)


            } catch (_: Exception) {
            }

            swipeMotionFragment.setOnRefreshListener {
                adapter.retry()
                swipeMotionFragment.isRefreshing = false
            }


        }




        makeAPIRequest()


        adapter.addLoadStateListener { loadState ->
            binding.apply {
                try {
                    progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                    recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                    textViewError.isVisible = loadState.source.refresh is LoadState.Error
                    imageViewErrorWifi.isVisible = loadState.source.refresh is LoadState.Error


                    if (loadState.source.refresh is LoadState.Error) {
                        attemptRequestAgain(seconds)
                        editor.clear()
                        editor.apply()

                    } else {
                        editor.putString("search", "SavedData")
                        editor.apply()

                    }

                    // empty view

                    if (loadState.source.refresh is LoadState.NotLoading &&
                        loadState.append.endOfPaginationReached &&
                        adapter.itemCount < 1
                    ) {
                        recyclerView.isVisible = false
                        textViewEmpty.isVisible = true
                        editor.clear()
                        editor.apply()
                        makeAPIRequest()
                        countdownTimer.cancel()

                    } else {
                        textViewEmpty.isVisible = false
                    }

                } catch (_: Exception) {
                }
            }

        }


        makeScrolling()


    }

    private fun makeScrolling() {


        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount

                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                    adapter.retry()
                }
            }
        })
    }


    // for etails frgaments
    override fun onItemClickedMe(photo: UnsplashPhoto) {

        val getUrl = photo.url.toString()
        val titles = photo.title.toString()

        val artcileId = photo.publishedAt.toString()
        saveUserProfilesAndNewsArticleId(artcileId, "My_Main_Home_Fragment")

        val intent = Intent(Constants.Main_Home_Fragment)
        intent.putExtra("Navigation", "Navigate_To_Detail_Fragment")
        intent.putExtra("titles", titles)
        intent.putExtra("getUrl", getUrl)
        requireContext().sendBroadcast(intent)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onResume() {
        super.onResume()
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
    }


    private fun attemptRequestAgain(seconds: Long) {
        countdownTimer = object : CountDownTimer(seconds * 1010, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                try {
                    adapter.retry()
                    countdownTimer.cancel()
                    this@GalleryFragment.seconds = 4
                } catch (_: Exception) {
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {

                try {
                    binding.textViewError.text =
                        "Something went wrong, unable retrieve data, Please Check Internet Connectivity...\nAuto try in: ${millisUntilFinished / 1000}"

                } catch (_: Exception) {
                }
            }
        }
        countdownTimer.start()
    }

    private fun makeAPIRequest() {

        val urlData = sharedDass.getString("search", "")

        if (urlData != "SavedData") {
            getSelectedCountry().let {
                viewModel.updateCountry(it)
            }
        }


        viewModel.photos.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }


    }

    private val broadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "Gallery_Fragment") {
                val newCountry = intent.getStringExtra("New_country")
                newCountry?.let {
                    binding.progressBar.isVisible = true
                    binding.textViewError.isVisible = false
                    binding.imageViewErrorWifi.isVisible = false
                    binding.recyclerView.isVisible = false
                    handler.postDelayed(Runnable {
                        viewModel.updateCountry(it)
                    }, 2000)
                }


                val newCategory = intent.getStringExtra("New_Category")
                newCategory?.let {
                    binding.progressBar.isVisible = true
                    binding.textViewError.isVisible = false
                    binding.imageViewErrorWifi.isVisible = false
                    binding.recyclerView.isVisible = false
                    handler.postDelayed(Runnable {
                        viewModel.updateCategory(it)
                    }, 2000)
                }

            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            requireContext().unregisterReceiver(broadcastReceiver)

            _binding = null
            handler.removeCallbacksAndMessages(null)
        } catch (_: Exception) {
        }
    }

    override fun onITemShortAdded(photo: UnsplashPhoto) {

        if (photo.isSaved) {
            removeArticleFromDatabase(photo)

        } else {
            savedToDatabase(photo)

        }


    }

    private fun removeArticleFromDatabase(photo: UnsplashPhoto) {
        photo.isSaved = false
        mUserViewModel.delete(photo)

        showSnackbar_Flaged_Report("Article Removed Successfully", photo)


    }

    private fun savedToDatabase(photo: UnsplashPhoto) {
        photo.isSaved = true

        val titles = photo.title.toString()
        val getUrl = photo.url.toString()
        val urlToImage = photo.urlToImage.toString()
        val name = photo.source.name.toString()
        val date = photo.publishedAt.toString()

        val _userName = Source("", name)
        val artciles = UnsplashPhoto(titles, getUrl, urlToImage, _userName, date)
        mUserViewModel.insert(artciles)


        showSnackbar_show_simple("Article Saved Successfully")


    }


    // for comment frgament
    override fun onBsItem(photo: UnsplashPhoto) {

        val artcileId = photo.publishedAt.toString()
        saveUserProfilesAndNewsArticleId(artcileId, "My_Main_Home_Fragment")
        val customPopupFragment = CommentFragment()
        customPopupFragment.show(childFragmentManager, customPopupFragment.tag)

    }


// we pass article id to bottomsheet comment

    private fun saveUserProfilesAndNewsArticleId(newsArticleId: String, whatFragment: String) {

        editor = sharedPref.edit()
        editor.putString("newsArticleId", newsArticleId)
        editor.putString("WhatFragment", whatFragment)
        editor.apply()
    }


    private fun handleSearchNavigation(handleSearchNavigation: String) {
        editor = sharedHandleSearchNavigation.edit()
        editor.putString("handleSearchNavigation", handleSearchNavigation)
        editor.apply()
    }


    private fun getSelectedCountry(): String {
        val sharedPref = requireContext().getSharedPreferences(
            Constants.search_Query_Shared_Prefs,
            Context.MODE_PRIVATE
        )
        return sharedPref.getString("selected_country", "us") ?: "us"
    }

    private fun showSnackbar_Flaged_Report(text: String, photo: UnsplashPhoto) {

        if (settings.getTheme() == AppSettings.THEME_LIGHT) {

            val snackbar = Snackbar.make(binding.recyclerView, text, 3000)

            snackbar.setTextColor(requireContext().resources.getColor(R.color.black))
            snackbar.setBackgroundTint(requireContext().resources.getColor(R.color.white))
            snackbar.setActionTextColor(requireContext().resources.getColor(R.color.black))
            snackbar.setAction(
                "UNDO"
            ) { view ->
                savedToDatabase(photo)
            }
            snackbar.show()

        } else {
            val snackbar = Snackbar.make(binding.recyclerView, text, 3000)
            snackbar.setTextColor(requireContext().resources.getColor(R.color.white))
            snackbar.setBackgroundTint(requireContext().resources.getColor(R.color.black))
            snackbar.setActionTextColor(requireContext().resources.getColor(R.color.white))
            snackbar.setAction(
                "UNDO"
            ) { view ->
                savedToDatabase(photo)
            }
            snackbar.show()

        }


    }


    private fun showSnackbar_show_simple(text: String) {

        if (settings.getTheme() == AppSettings.THEME_LIGHT) {

            val snackbar = Snackbar.make(binding.recyclerView, text, 3000)

            snackbar.setTextColor(requireContext().resources.getColor(R.color.black))
            snackbar.setBackgroundTint(requireContext().resources.getColor(R.color.white))
            snackbar.setActionTextColor(requireContext().resources.getColor(R.color.black))
            snackbar.show()

        } else {
            val snackbar = Snackbar.make(binding.recyclerView, text, 3000)
            snackbar.setTextColor(requireContext().resources.getColor(R.color.white))
            snackbar.setBackgroundTint(requireContext().resources.getColor(R.color.black))
            snackbar.setActionTextColor(requireContext().resources.getColor(R.color.white))
            snackbar.show()

        }


    }


}




