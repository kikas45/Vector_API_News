package com.example.vectonews.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.vectonews.R
import com.example.vectonews.databinding.FragmentMainSaveBinding


class Main_Save_Fragment : Fragment(R.layout.fragment_main_save) {


    private var _binding: FragmentMainSaveBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainSaveBinding.bind(view)
        showKeyBoard()



     //   changeToolabrColor()

        binding.btnDeleteall.setOnClickListener {
            hideKeyBaord()

        }

        binding.apply {

            btnBackPressed.setOnClickListener {
                findNavController().popBackStack(R.id.main_Home_Fragment, false)
                hideKeyBaord()

            }


            editText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val editUrl = binding.editText.text.toString().trim()
                    hideKeyBaord()
                    if (!editUrl.isEmpty()) {

                        view.findNavController()
                            .navigate(R.id.action_main_Save_Fragment_to_main_Home_Fragment)

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

      //  binding.constraintLayout.setBackgroundColor(resources.getColor(R.color.deem_shade))

        val newStatusBarColor = Color.parseColor("#E2E6DE")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.statusBarColor = newStatusBarColor
        }

    }
}