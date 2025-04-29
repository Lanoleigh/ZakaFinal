package com.example.zakazaka.Views.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.zakazaka.R
import com.example.zakazaka.Views.CreateCategory


class CategoryFragment : Fragment() {




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_category, container, false)


        val txtCategoryNav = view.findViewById<TextView>(R.id.createCategory)
        txtCategoryNav.setOnClickListener {
            val intent = Intent(requireContext(), CreateCategory::class.java)
            startActivity(intent)
        }

        return view
    }

}