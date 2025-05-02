package com.example.zakazaka.Views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import com.example.zakazaka.ViewModels.TransactionViewModel
import com.example.zakazaka.ViewModels.ViewModelFactory

class AccountActivity : AppCompatActivity() {
    lateinit var recentTransInAccRecView: RecyclerView
    lateinit var transactionViewModel : TransactionViewModel
    lateinit var transactionAdapter : TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val factory = ViewModelFactory(
            UserRepository(AppDatabase.getDatabase(this).userDao()),
            AccountRepository(AppDatabase.getDatabase(this).accountDao()),
            BudgetGoalRepository(AppDatabase.getDatabase(this).budgetGoalDao()),
            CategoryRepository(AppDatabase.getDatabase(this).categoryDao()),
            SubCategoryRepository(AppDatabase.getDatabase(this).subCategoryDao()),
            TransactionRepository(AppDatabase.getDatabase(this).transactionDao())
        )
        transactionViewModel = ViewModelProvider(this,factory)[TransactionViewModel::class.java]

        recentTransInAccRecView = findViewById(R.id.recentTransInAccRecView)
        recentTransInAccRecView.layoutManager = LinearLayoutManager(this)

        transactionViewModel.getAllTransactions().observe(this){ transactions ->
            val latestTransactions = transactions.sortedByDescending { it.date }.take(3)
            transactionAdapter = TransactionAdapter(latestTransactions){}
            recentTransInAccRecView.adapter = transactionAdapter
        }
        val btnAddAcc = findViewById<Button>(R.id.btnAddAccount)
        btnAddAcc.setOnClickListener{
            val intent = Intent(this,AddAccountActivity::class.java)
            startActivity(intent)
        }
        val accBackbtn = findViewById<ImageView>(R.id.btnAccBack)
        accBackbtn.setOnClickListener {
            finish()
        }
    }
}