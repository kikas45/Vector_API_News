package com.example.vectonews.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vectonews.R
import com.example.vectonews.databinding.FragmentSearchHistoryBinding
import com.example.vectonews.searchHistory.User
import com.example.vectonews.searchHistory.UserViewModel
import com.example.vectonews.settings.AppSettings
import com.example.vectonews.ui.searchFragment.ListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Main_Search_History_Fragment : Fragment(R.layout.fragment_search_history),
    ListAdapter.OnItemClickListener, ListAdapter.OnItemLongClickListener {


    private var CHECK_IF_EXIST = "CHECK_MY_STATE_OF_SEARCH"

    private val mUserViewModel by viewModels<UserViewModel>()

    private var _binding: FragmentSearchHistoryBinding? = null
    private val binding get() = _binding!!

    private val settings: AppSettings by lazy {
        AppSettings(requireContext().applicationContext)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchHistoryBinding.bind(view)
        showKeyBoard()



        changeToolabrColor()

        binding.textDeleteall.setOnClickListener {
            deleteAllUsers()
            hideKeyBaord()
        }

        DisplayMySearch()

        binding.apply {

            btnBackPressed.setOnClickListener {
                findNavController().popBackStack(R.id.main_Home_Fragment, false)
                hideKeyBaord()


            }


            val bundle = Bundle()


            val sharedDatasss = view.context.applicationContext.getSharedPreferences(
                CHECK_IF_EXIST,
                Context.MODE_PRIVATE
            )

            val editor = sharedDatasss.edit()

            editText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val editUrl = binding.editText.text.toString().trim()
                    hideKeyBaord()

                    if (!editUrl.isEmpty()) {
                        insertDataToDatabase(editUrl)
                        mUserViewModel.deleteExcessItems()
                        bundle.putString("search", editUrl)
                        editor.putString("search", "SavedData")
                        editor.apply()
                        //  view.findNavController().navigate(R.id.action_searchHistoryFragment_to_searchFragment, bundle)
                        view.findNavController().popBackStack(R.id.main_Home_Fragment, false)

                    } else {
                        Toast.makeText(context, "Input text for search", Toast.LENGTH_SHORT).show()
                    }
                    return@OnEditorActionListener true
                }
                false
            })


        }



        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length > 0) {
                    binding.editClear.visibility = View.VISIBLE
                } else {
                    binding.editClear.visibility = View.INVISIBLE
                }
            }
        })

        binding.editClear.setOnClickListener { v -> binding.editText.text.clear() }


    }


    private fun showKeyBoard() {
        try {
            binding.editText.requestFocus()
            val imm =
                requireContext().applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        } catch (ignored: Exception) {
        }
    }

    private fun hideKeyBaord() {
        try {
            val imm =
                requireContext().applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editText.windowToken, 0)
        } catch (ignored: Exception) {
        }
    }


    @SuppressLint("ObsoleteSdkInt")
    private fun changeToolabrColor() {


        if (settings.getTheme() == AppSettings.THEME_LIGHT) {
            activity?.window?.statusBarColor = Color.parseColor("#E8EDFA")

        } else {
            activity?.window?.statusBarColor = Color.parseColor("#2E2E2E")

        }


    }


    private fun insertDataToDatabase(name: String) {

        val user = User( name)
        mUserViewModel.addUser(user)

    }


    private fun DisplayMySearch() {
        // Recyclerview
        val adapter = ListAdapter(this, this)


        binding.apply {
            recyclerview.adapter = adapter
            recyclerview.layoutManager = LinearLayoutManager(requireContext())

        }
        // UserViewModel
        mUserViewModel.readAllData.observe(viewLifecycleOwner, Observer { user ->
            adapter.setData(user)

        })


    }


    override fun onItemLongClicked(photo: User) {

        val name = photo.firstName.toString()
        val user = User(name)

        deleteUser(user)
        hideKeyBaord()


    }


    private fun deleteUser(photo: User) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mUserViewModel.deleteUser(photo)
            Toast.makeText(
                requireContext(),
                "Successfully removed: ${photo.firstName.toString()}",
                Toast.LENGTH_SHORT
            ).show()

        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete ${photo.firstName.toString()}?")
        builder.setMessage("Are you sure you want to delete ${photo.firstName.toString()}?")
        builder.create().show()
    }

    private fun deleteAllUsers() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mUserViewModel.deleteAllUsers()
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


    override fun onItemClicked(_user: User) {
        val name = _user.firstName.toString()

        val sharedDatasss = view?.context?.applicationContext?.getSharedPreferences(
            CHECK_IF_EXIST,
            Context.MODE_PRIVATE
        )
        val editor = sharedDatasss?.edit()

        val bundle = Bundle().apply { putString("search", name) }

        editor?.putString("search", "SavedData")
        editor?.apply()

        //   view?.findNavController()?.navigate(R.id.action_searchHistoryFragment_to_searchFragment, bundle)

        hideKeyBaord()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            _binding = null
        } catch (_: Exception) {
        }
    }


}