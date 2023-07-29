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
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vectonews.R
import com.example.vectonews.api.UnsplashPhoto
import com.example.vectonews.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery), NewsAdapter.OnItemClickListenerMe, NewsAdapter.OnShortClickedAddItem {

    lateinit var countdownTimer: CountDownTimer
    private var seconds = 3L

    private val viewModel by viewModels<GalleryViewModel>()

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val adapter: NewsAdapter by lazy {
        NewsAdapter(this, this)
    }

    private val sharedDass: SharedPreferences by lazy {
        requireContext().applicationContext.getSharedPreferences(
            "PASS_DATA_TRANS_FRAGMENT",
            Context.MODE_PRIVATE
        )
    }

    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
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

            profileImage.setOnClickListener {
                Toast.makeText(requireContext(), "Pending", Toast.LENGTH_SHORT).show()
            }


            textsearch.setOnClickListener {
                val intent = Intent("Main_Home_Fragment")
                intent.putExtra("Navigation", "Navigate_to_SearchHistoryFragment")
                requireContext().sendBroadcast(intent)

            }

        }


        val editor = sharedDass.edit()
        val urlData = sharedDass.getString("search", "")

        if (urlData != "SavedData") {
            viewModel.updateCountry("us")
        }


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


    override fun onItemClickedMe(photo: UnsplashPhoto) {

        val getUrl = photo.url.toString()
        val titles = photo.title.toString()

        val intent = Intent("Main_Home_Fragment")
        intent.putExtra("Navigation", getUrl)
        intent.putExtra("titles", titles)
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
                        "Something went wrong, unable retrieve data...\nTrying again in: ${millisUntilFinished / 1000}"

                } catch (_: Exception) {
                }
            }
        }
        countdownTimer.start()
    }

    private fun makeAPIRequest() {
        // viewModel.updateCountry("ng")

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
                    binding.recyclerView.isVisible = false
                    handler.postDelayed(Runnable {
                        viewModel.updateCountry(it)
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
        val getUrl = photo.url.toString()
        val titles = photo.title.toString()
        val urlToImage = photo.urlToImage.toString()

        Toast.makeText(requireContext(), "${titles.toString()}", Toast.LENGTH_SHORT).show()


    }


}




