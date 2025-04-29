package com.example.zakazaka.Views.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.zakazaka.R
import com.example.zakazaka.Views.AddTransaction


class TransactionFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transaction, container, false)

        val btnAddTransactionPage =  view.findViewById<Button>(R.id.bAddNewTransactions)
        btnAddTransactionPage.setOnClickListener{
            val intent = Intent(requireContext(),AddTransaction::class.java)
            startActivity(intent)
        }

        return view
    }

}