package com.example.vectonews.ui.sub_main


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vectonews.R
import com.example.vectonews.api.Source
import com.example.vectonews.api.UnsplashPhoto
import com.example.vectonews.databinding.FragmentSavedBinding
import com.example.vectonews.offlinecenter.SavedDetailAdapter
import com.example.vectonews.offlinecenter.SavedViewModel
import com.example.vectonews.searchHistory.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedFragment : Fragment(R.layout.fragment_saved),
    SavedDetailAdapter.OnItemClickListenerDetails, SavedDetailAdapter.OnItemLongClickListenerSaved {

    private val mUserViewModel by viewModels<SavedViewModel>()

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!


    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }


    private val adapter: SavedDetailAdapter by lazy {
        SavedDetailAdapter(this, this)
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

                profileImage.setOnClickListener {
                    Toast.makeText(requireContext(), "Pending", Toast.LENGTH_SHORT).show()

                }




                textsearch.setOnClickListener {
                    val intent = Intent("Main_Home_Fragment")
                    intent.putExtra("Navigation", "Navigate_to_SearchHistoryFragment")
                    requireContext().sendBroadcast(intent)

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
            adapter.setData(it)
        })


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onResume() {
        super.onResume()
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //  view?.findNavController()?.popBackStack(R.id.galleryFragment, false)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
    }

    override fun onclickDetailsItem(photo: UnsplashPhoto) {
        val bundle = Bundle().apply {

            putString("title", photo.title.toString())
            putString("url", photo.url.toString())
            putString("urlToImage", photo.urlToImage.toString())
        }

        //  view?.findNavController()?.navigate(R.id.action_savedFragment_to_detailsFragment, bundle)

    }

    override fun onItemLongClickedSaved(photo: UnsplashPhoto) {

        deleteAllUsers(photo)
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
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
        builder.setTitle("Delete everything?")
        builder.setMessage("Are you sure you want to delete everything?")
        builder.create().show()
    }



}

