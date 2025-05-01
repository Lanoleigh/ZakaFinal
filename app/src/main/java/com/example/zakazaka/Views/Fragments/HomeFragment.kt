package com.example.zakazaka.Views.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zakazaka.Adapters.TransactionAdapter
import com.example.zakazaka.Data.Database.AppDatabase
import com.example.zakazaka.R
import com.example.zakazaka.Repository.AccountRepository
import com.example.zakazaka.Repository.BudgetGoalRepository
import com.example.zakazaka.Repository.CategoryRepository
import com.example.zakazaka.Repository.SubCategoryRepository
import com.example.zakazaka.Repository.TransactionRepository
import com.example.zakazaka.Repository.UserRepository
import com.example.zakazaka.ViewModels.BudgetGoalViewModel
import com.example.zakazaka.ViewModels.TransactionViewModel
import com.example.zakazaka.ViewModels.ViewModelFactory
import com.example.zakazaka.Views.AccountActivity

class HomeFragment : Fragment() {
    private lateinit var recentTransactionRecyclerView: RecyclerView
    private lateinit var transactionAdpater : TransactionAdapter
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var budgetGoalViewModel: BudgetGoalViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
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

        transactionViewModel = ViewModelProvider(this, factory)[TransactionViewModel::class.java]
        budgetGoalViewModel = ViewModelProvider(this, factory)[BudgetGoalViewModel::class.java]

        //handles the recycler view for transactions showing the lastests 2 transactions
        recentTransactionRecyclerView = view.findViewById(R.id.recentTransactionRecyclerView)
        recentTransactionRecyclerView.layoutManager = LinearLayoutManager(requireContext())

       transactionViewModel.getAllTransactions().observe(viewLifecycleOwner) { transactions ->
           val latestTransactions = transactions.sortedByDescending { it.date }.take(3)
           transactionAdpater = TransactionAdapter(latestTransactions){ transaction ->
               Toast.makeText(requireContext(), "Clicked on ${transaction.description}", Toast.LENGTH_SHORT).show()
           }

           recentTransactionRecyclerView.adapter = transactionAdpater
       }

        budgetGoalViewModel.getAllBudgetGoals().observe(viewLifecycleOwner) { budgetGoals ->
            val latestBudgetGoals = budgetGoals.takeLast(1)
            latestBudgetGoals.forEach { budgetGoal ->
                view.findViewById<TextView>(R.id.txtTotalMonthlyBudget).text = "Total Monthly Budget R${budgetGoal.maxAmount.toString()}"
            }
        }
        val btnDigitalAccount = view.findViewById<Button>(R.id.btnDigitalAccount)
        btnDigitalAccount.setOnClickListener {
            val intent = Intent(activity, AccountActivity::class.java)
            startActivity(intent)
        }

    }


}