package com.example.vectonews.ui.onItemSearched

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.disklrucache.DiskLruCache.Editor
import com.example.vectonews.R
import com.example.vectonews.api.Source
import com.example.vectonews.api.UnsplashPhoto
import com.example.vectonews.comments.CommentFragment
import com.example.vectonews.databinding.FragmentSearchBinding
import com.example.vectonews.offlinecenter.SavedViewModel
import com.example.vectonews.settings.AppSettings
import com.example.vectonews.ui.sub_main.HistoryViewModel
import com.example.vectonews.ui.sub_main.NewsAdapter
import com.example.vectonews.ui.sub_main.NewsLoadStateAdapter
import com.example.vectonews.util.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search), NewsAdapter.OnItemClickListenerMe,
    NewsAdapter.OnBsClickItem,
    NewsAdapter.OnShortClickedAddItem {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!


    private val viewModel by viewModels<HistoryViewModel>()
    private val mUserViewModel by viewModels<SavedViewModel>()

    private val sharedDass: SharedPreferences by lazy {
        requireContext().getSharedPreferences(
            Constants.CHECK_IF_EXIST,
            Context.MODE_PRIVATE
        )
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

    private val adapter: NewsAdapter by lazy {
        NewsAdapter(
            this, this, this, mUserViewModel, viewLifecycleOwner
        )
    }

    private val sharedPrefPassDataToDetailsFragment: SharedPreferences by lazy {
        requireContext().getSharedPreferences(
            Constants.PASS_DATA_TO_DETAIL_FRAGMENT,
            Context.MODE_PRIVATE
        )

    }


    private val settings: AppSettings by lazy {
        AppSettings(requireContext().applicationContext)
    }


    @SuppressLint("CommitPrefEdits")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        changeToolabrColor()


        val urls = arguments?.getString("search") + ""
        val urlData = sharedDass.getString("search_Check", "")

        if (urlData != "SavedData") {
            binding.recyclerView.scrollToPosition(0)
            viewModel.searchNews(urls)
            binding.editText.text = urls
        } else {
            binding.editText.text = urls
        }


        binding.apply {

            editText.setOnClickListener {
                view.findNavController().navigate(R.id.action_searchFragment_to_main_Save_Fragment)
                handleSearchNavigation("SearchFragment")
            }

            editClear.setOnClickListener {
                view.findNavController().navigate(R.id.action_searchFragment_to_main_Save_Fragment)
                handleSearchNavigation("SearchFragment")
            }

            btnBackPressed.setOnClickListener {
                view.findNavController().popBackStack(R.id.main_Home_Fragment, false)
            }


            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.itemAnimator = null
            // retry for head and footer
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = NewsLoadStateAdapter { adapter.retry() },
                footer = NewsLoadStateAdapter { adapter.retry() }

            )

            // btn retry from Frgament UI
            buttonRetry.setOnClickListener { adapter.retry() }

        }



        viewModel.photos.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)

        }
        val editoree = sharedDass.edit()
        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error

                if (loadState.source.refresh is LoadState.Error) {
                    editoree.clear()
                    editoree.apply()

                } else {
                    editoree.putString("search_Check", "SavedData")
                    editoree.apply()

                }


                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }

            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onResume() {
        super.onResume()

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view?.findNavController()?.popBackStack(R.id.main_Home_Fragment, false)
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


    override fun onItemClickedMe(photo: UnsplashPhoto) {
        val getUrl ="" +  photo.url
        val titles = "" + photo.title
        val urlToImage ="" + photo.urlToImage
        val name = "" + photo.source.name
        val publishedAt = "" + photo.publishedAt
        val artcileId = "" + photo.publishedAt

        sharedPrefPassDataToDetailsFragment(titles, getUrl, urlToImage, name, publishedAt)

        saveUserProfilesAndNewsArticleId(artcileId, "Search_Fragment")
        val bundle = Bundle().apply {
            putString("urls_webView", getUrl.toString())
            putString("titles", titles.toString()) // for web view
        }

        view?.findNavController()?.navigate(R.id.action_searchFragment_to_detailFragment, bundle)


    }

    override fun onITemShortAdded(photo: UnsplashPhoto) {
        if (photo.isSaved) {
            removeArticleFromDatabase(photo)

        } else {
            savedToDatabase(photo)

        }

    }

    override fun onBsItem(photo: UnsplashPhoto) {
        val artcileId = "" + photo.publishedAt
        saveUserProfilesAndNewsArticleId(artcileId, "Search_Fragment")
        val customPopupFragment = CommentFragment()
        customPopupFragment.show(childFragmentManager, customPopupFragment.tag)
    }

    private fun saveUserProfilesAndNewsArticleId(newsArticleId: String, whatFragment: String) {

        val editor = sharedPref.edit()
        editor.putString("newsArticleId", newsArticleId)
        editor.putString("WhatFragment", whatFragment)
        editor.apply()
    }


    private fun savedToDatabase(photo: UnsplashPhoto) {
        photo.isSaved = true
        val titles = "" + photo.title
        val getUrl = "" + photo.url
        val urlToImage = "" + photo.urlToImage
        val name = "" + photo.source.name
        val date = "" + photo.publishedAt

        val _userName = Source("", name)
        val artciles = UnsplashPhoto(titles, getUrl, urlToImage, _userName, date)
        mUserViewModel.insert(artciles)

        Snackbar.make(binding.recyclerView, "Article Saved Successfully", Snackbar.LENGTH_SHORT)
            .show()

    }

    private fun removeArticleFromDatabase(photo: UnsplashPhoto) {
        photo.isSaved = false
        mUserViewModel.delete(photo)

        Snackbar.make(binding.recyclerView, "Article Removed Successfully", Snackbar.LENGTH_SHORT)
            .setAction("Undo", View.OnClickListener {
                savedToDatabase(photo)
            })
            .setActionTextColor(resources.getColor(R.color.red_color))


            .show()


    }

    private fun handleSearchNavigation(handleSearchNavigation: String) {

        val editor = sharedHandleSearchNavigation.edit()
        editor.putString("handleSearchNavigation", handleSearchNavigation)
        editor.apply()
    }


    @SuppressLint("CommitPrefEdits")
    fun sharedPrefPassDataToDetailsFragment(
        titles: String,
        getUrl: String,
        urlToImage: String,
        name: String,
        date: String,
    ) {
        val editor = sharedPrefPassDataToDetailsFragment.edit()
        editor.putString("titles", titles)
        editor.putString("getUrl", getUrl)
        editor.putString("urlToImage", urlToImage)
        editor.putString("name", name)
        editor.putString("date", date)
        editor.apply()


    }


}