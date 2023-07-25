package com.example.vectonews.ui.sub_main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import com.example.vectonews.R


class SavedFragment : Fragment(R.layout.fragment_saved) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textview = view.findViewById<TextView>(R.id.textView)


        textview.setOnClickListener {
            val intent = Intent("Main_Home_Fragment")
            requireContext().sendBroadcast(intent)

        }


    }
}