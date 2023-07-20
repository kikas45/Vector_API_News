package com.example.vectonews.ui.small

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.vectonews.R


class Small_One : Fragment(R.layout.fragment_small_one) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val textView = view.findViewById<TextView>(R.id.txtSmallOne)

        textView.setOnClickListener {
            findNavController().navigate(R.id.action_small_One_to_small_Two)
        }



    }

}