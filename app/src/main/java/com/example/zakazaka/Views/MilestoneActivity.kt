package com.example.zakazaka.Views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.zakazaka.Data.Database.AppDatabase
import com.example.zakazaka.Models.BudgetGoalEntity
import com.example.zakazaka.R
import com.example.zakazaka.Repository.AccountRepository
import com.example.zakazaka.Repository.BudgetGoalRepository
import com.example.zakazaka.Repository.CategoryRepository
import com.example.zakazaka.Repository.SubCategoryRepository
import com.example.zakazaka.Repository.TransactionRepository
import com.example.zakazaka.Repository.UserRepository
import com.example.zakazaka.ViewModels.AccountViewModel
import com.example.zakazaka.ViewModels.BudgetGoalViewModel
import com.example.zakazaka.ViewModels.CategoryViewModel
import com.example.zakazaka.ViewModels.HowToViewModel
import com.example.zakazaka.ViewModels.ViewModelFactory
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class MilestoneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_milestone)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val howtoViewModel = HowToViewModel()
        howtoViewModel.accountViewModel = AccountViewModel(AccountRepository(AppDatabase.getDatabase(this).accountDao()))
        howtoViewModel.budgetGoalViewModel = BudgetGoalViewModel(BudgetGoalRepository(AppDatabase.getDatabase(this).budgetGoalDao()))
        howtoViewModel.categoryViewModel = CategoryViewModel(CategoryRepository(AppDatabase.getDatabase(this).categoryDao()))
        //initalizes the viewModelFactory
        val factory = ViewModelFactory(
            UserRepository(AppDatabase.getDatabase(this).userDao()),
            AccountRepository(AppDatabase.getDatabase(this).accountDao()),
            BudgetGoalRepository(AppDatabase.getDatabase(this).budgetGoalDao()),
            CategoryRepository(AppDatabase.getDatabase(this).categoryDao()),
            SubCategoryRepository(AppDatabase.getDatabase(this).subCategoryDao()),
            TransactionRepository(AppDatabase.getDatabase(this).transactionDao())
        )

        val budgetGoalViewModel = ViewModelProvider(this,factory)[BudgetGoalViewModel::class.java]
        val month = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val sharedPref = getSharedPreferences("BudgetAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getLong("LOGGED_USER_ID", 0)

        budgetGoalViewModel.getAllBudgetGoals().observe(this) { budgetGoals ->
            val latestBudgetGoals = budgetGoals.takeLast(1)
            latestBudgetGoals.forEach { budgetGoal ->
                if(budgetGoal.month == month){
                    findViewById<TextView>(R.id.txtWarning).visibility = View.VISIBLE
                }
            }
        }
        val btnSaveGoal = findViewById<Button>(R.id.btnSaveBudgetGoal)
        btnSaveGoal.setOnClickListener {
            val minimumBudget = findViewById<EditText>(R.id.edMinimumBudget).text.toString()
            val maximumBudget = findViewById<EditText>(R.id.budgetGoalInput).text.toString()

            if(maximumBudget.isNotEmpty() && minimumBudget.isNotEmpty()){
                val budgetGoal = BudgetGoalEntity(
                    minAmount = minimumBudget.toDouble(),
                    maxAmount = maximumBudget.toDouble(),
                    month = month,
                    status = "Beginner",
                    userID = userId
                )
                budgetGoalViewModel.addBudgetGoal(budgetGoal).observe(this){
                    if(it > 0){
                        Toast.makeText(this,"Budget Goal Created",Toast.LENGTH_SHORT).show()
                        howtoViewModel.isHowtoCompleted(userId,this) { completed ->
                            if (!completed) {
                                val intent = Intent(this, HowToGetStarted::class.java)
                                startActivity(intent)
                            }else{
                                val intent = Intent(this, Dashboard::class.java)
                                startActivity(intent)
                            }
                        }
                    }else{
                        Toast.makeText(this,"Error Creating Budget Goal",Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }

        val btnMilestoneBack = findViewById<ImageView>(R.id.btnMilestoneBack)
        btnMilestoneBack.setOnClickListener {
            finish()
        }



    }
}