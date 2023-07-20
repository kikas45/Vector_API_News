package com.example.vectonews.ui.sub_main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.vectonews.R


class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textview = view.findViewById<TextView>(R.id.textView3)


        textview.setOnClickListener {
            val intent = Intent("Main_Home_Fragment")
            requireContext().sendBroadcast(intent)

        }


    }
}