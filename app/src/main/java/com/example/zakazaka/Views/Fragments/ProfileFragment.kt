package com.example.zakazaka.Views.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.zakazaka.R
import com.example.zakazaka.Views.AddAccountActivity


class ProfileFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_user, container, false)
        val btnAddAcc = view.findViewById<Button>(R.id.btnAddAccountP)
        btnAddAcc.setOnClickListener {
            val intent = Intent(activity, AddAccountActivity::class.java)
            startActivity(intent)
        }
        return view
    }

}