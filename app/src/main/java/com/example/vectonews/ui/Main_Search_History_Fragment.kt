package com.example.vectonews.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
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
import androidx.activity.OnBackPressedCallback
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
import com.example.vectonews.ui.searchFragment.HistorySearchAdapter
import com.example.vectonews.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Main_Search_History_Fragment : Fragment(R.layout.fragment_search_history),
    HistorySearchAdapter.OnItemClickListener, HistorySearchAdapter.OnItemLongClickListener {


    private val mUserViewModel by viewModels<UserViewModel>()

    private var _binding: FragmentSearchHistoryBinding? = null
    private val binding get() = _binding!!

    private val settings: AppSettings by lazy {
        AppSettings(requireContext().applicationContext)
    }

    private val sharedDatasss: SharedPreferences by lazy { requireContext().getSharedPreferences(
            Constants.CHECK_IF_EXIST,
            Context.MODE_PRIVATE
        )

    }


    private val sharedHandleSearchNavigation: SharedPreferences by lazy {
        requireContext().getSharedPreferences(Constants.handleSearchNavigation, Context.MODE_PRIVATE)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchHistoryBinding.bind(view)

        showKeyBoard()

        val editor = sharedDatasss.edit()
        editor.clear()
        editor.apply()

        changeToolabrColor()

        binding.textDeleteall.setOnClickListener {
            deleteAllUsers()
            hideKeyBaord()
        }

        DisplaySearchHistory()

        binding.apply {

            btnBackPressed.setOnClickListener {
                val previous_Fragment = sharedHandleSearchNavigation.getString("handleSearchNavigation", "")
                if (previous_Fragment.equals("Major_Home_Fragment")){
                    findNavController().popBackStack(R.id.main_Home_Fragment, false)
                }else{
                    findNavController().popBackStack(R.id.searchFragment, false)
                    editor.putString("search_Check", "SavedData")
                    editor.apply()
                }

                hideKeyBaord()


            }


            val bundle = Bundle()

            editText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val editUrl = binding.editText.text.toString().trim()
                    hideKeyBaord()

                    if (!editUrl.isEmpty()) {
                        insertDataToDatabase(editUrl)
                        mUserViewModel.deleteExcessItems()
                        bundle.putString("search", editUrl)
                        view.findNavController().navigate(R.id.action_main_Save_Fragment_to_searchFragment, bundle)

                    } else {
                        showToast("Input text for search")
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

        val user = User(name)
        mUserViewModel.addUser(user)

    }


    private fun DisplaySearchHistory() {
        // Recyclerview
        val adapter = HistorySearchAdapter(this, this)


        binding.apply {
            recyclerview.adapter = adapter
            recyclerview.layoutManager = LinearLayoutManager(requireContext())

        }
        // UserViewModel
        mUserViewModel.readAllData.observe(viewLifecycleOwner, Observer { user ->
            adapter.setData(user)
            if (user.isEmpty()) {
                binding.textDeleteall.visibility = View.INVISIBLE
                binding.textView2.visibility = View.INVISIBLE
            } else {
                binding.textDeleteall.visibility = View.VISIBLE
                binding.textView2.visibility = View.VISIBLE
            }

        })


    }


    override fun onItemLongClicked(photo: User) {

        val name = ""+ photo.firstName
        val user = User(name)

        deleteUser(user)
        hideKeyBaord()


    }


    private fun deleteUser(photo: User) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mUserViewModel.deleteUser(photo)

            showToast("Successfully removed: ${photo.firstName}")
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete ${photo.firstName}?")
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
        val name =  ""+ _user.firstName
        val bundle = Bundle().apply { putString("search", name) }
        findNavController().navigate(R.id.action_main_Save_Fragment_to_searchFragment, bundle)
        hideKeyBaord()
    }


    override fun onResume() {
        super.onResume()
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val previous_Fragment = sharedHandleSearchNavigation.getString("handleSearchNavigation", "")

                if (previous_Fragment.equals("Major_Home_Fragment")){
                    findNavController().popBackStack(R.id.main_Home_Fragment, false)
                }else{
                    findNavController().popBackStack(R.id.searchFragment, false)
                    val editor = sharedDatasss.edit()
                    editor.putString("search_Check", "SavedData")
                    editor.apply()
                }

                hideKeyBaord()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            _binding = null
        } catch (_: Exception) {
        }
    }

    private fun showToast(message: String) {
        try {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        } catch (ignored: Exception) {
        }
    }

}