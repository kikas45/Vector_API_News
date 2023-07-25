package com.example.vectonews.ui.sub_main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vectonews.R
import com.example.vectonews.api.UnsplashPhoto
import com.example.vectonews.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery), ProductsAdapter.OnItemClickListenerMe {


    private val viewModel by viewModels<GalleryViewModel>()

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentGalleryBinding.bind(view)



        binding.apply {
            hamburger.setOnClickListener {

                val intent = Intent("MainActivity")
                view.context.sendBroadcast(intent)

            }

            textsearch.setOnClickListener {
                val intent = Intent("Main_Home_Fragment")
                requireContext().sendBroadcast(intent)
            }

        }



        val adapter = ProductsAdapter(this)


        val sharedDass = view.context.applicationContext.getSharedPreferences(
            "PASS_DATA_TRANS_FRAGMENT",
            Context.MODE_PRIVATE
        )
        val editor = sharedDass.edit()


        binding.apply {



            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.itemAnimator = null
            // retry for head and footer
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = UnsplashPhotoLoadStateAdapter { adapter.retry() },
                footer = UnsplashPhotoLoadStateAdapter { adapter.retry() }

            )

            // btn retry from Frgament UI
            buttonRetry.setOnClickListener { adapter.retry() }


            swipeMotionFragment.setOnRefreshListener {
                adapter.retry()
                swipeMotionFragment.isRefreshing = false
            }


        }

        viewModel.photos.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)

        }



        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error


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


    override fun onItemClickedMe(photo: UnsplashPhoto) {

        Toast.makeText(context, ""+ photo.title.toString(), Toast.LENGTH_SHORT).show()

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
}




