package com.example.zakazaka.Views.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zakazaka.Adapters.CategoryAdapter
import com.example.zakazaka.Adapters.TransactionAdapter
import com.example.zakazaka.Data.Database.AppDatabase
import com.example.zakazaka.R
import com.example.zakazaka.Repository.AccountRepository
import com.example.zakazaka.Repository.BudgetGoalRepository
import com.example.zakazaka.Repository.CategoryRepository
import com.example.zakazaka.Repository.SubCategoryRepository
import com.example.zakazaka.Repository.TransactionRepository
import com.example.zakazaka.Repository.UserRepository
import com.example.zakazaka.ViewModels.CategoryViewModel
import com.example.zakazaka.ViewModels.TransactionViewModel
import com.example.zakazaka.ViewModels.ViewModelFactory
import com.example.zakazaka.Views.AddTransaction
import com.example.zakazaka.Views.CategoryDetails


class TransactionFragment : Fragment() {
    lateinit var transactionViewModel : TransactionViewModel
    lateinit var transactionRecyclerView : RecyclerView
    lateinit var transactionAdapter : TransactionAdapter
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext().applicationContext
        val factory = ViewModelFactory(
            UserRepository(AppDatabase.getDatabase(context).userDao()),
            AccountRepository(AppDatabase.getDatabase(context).accountDao()),
            BudgetGoalRepository(AppDatabase.getDatabase(context).budgetGoalDao()),
            CategoryRepository(AppDatabase.getDatabase(context).categoryDao()),
            SubCategoryRepository(AppDatabase.getDatabase(context).subCategoryDao()),
            TransactionRepository(AppDatabase.getDatabase(context).transactionDao())
        )

        val sharedPref = requireContext().getSharedPreferences("BudgetAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getLong("LOGGED_USER_ID", 0)

        transactionViewModel = ViewModelProvider(this, factory)[TransactionViewModel::class.java]

        transactionRecyclerView = view.findViewById(R.id.recentTransRecyclerView)
        transactionRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        transactionViewModel.getAllTransactions().observe(viewLifecycleOwner){ transactions ->
            val latestTransactions = transactions.sortedByDescending { it.date }.take(3)
            transactionAdapter = TransactionAdapter(latestTransactions){}
            transactionRecyclerView.adapter = transactionAdapter

        }


    }

}