package com.example.zakazaka.Views

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.zakazaka.R
import com.example.zakazaka.ViewModels.HowToViewModel

import androidx.lifecycle.lifecycleScope

import com.example.zakazaka.Data.Database.AppDatabase
import com.example.zakazaka.Repository.AccountRepository
import com.example.zakazaka.Repository.BudgetGoalRepository
import com.example.zakazaka.Repository.CategoryRepository
import com.example.zakazaka.ViewModels.AccountViewModel
import com.example.zakazaka.ViewModels.BudgetGoalViewModel
import com.example.zakazaka.ViewModels.CategoryViewModel
import kotlinx.coroutines.launch

class HowToGetStarted : AppCompatActivity() {
    var hasAccount: Boolean = false
    var hasCategory: Boolean = false
    var hasBudgetGoal: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_how_to_get_started)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.transMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val userId = intent.getLongExtra("USER_ID", -1)

        val howToViewModel = HowToViewModel()
        howToViewModel.accountViewModel = AccountViewModel(AccountRepository(AppDatabase.getDatabase(this).accountDao()))
        howToViewModel.budgetGoalViewModel = BudgetGoalViewModel(BudgetGoalRepository(AppDatabase.getDatabase(this).budgetGoalDao()))
        howToViewModel.categoryViewModel = CategoryViewModel(CategoryRepository(AppDatabase.getDatabase(this).categoryDao()))


        //fetches the suspended methods in a coroutine to avoid blocking the UI thread
        lifecycleScope.launch {
            hasAccount = howToViewModel.checkForAccount(userId)
            hasCategory = howToViewModel.checkForCategory(userId)
            hasBudgetGoal = howToViewModel.checkForBudgetGoal(userId)
        }
        //checking if user has set up the account, category and budget goal
        val txtSetUpAccount = findViewById<TextView>(R.id.txtSetUpAccount)
        txtSetUpAccount.setOnClickListener {
            if (!hasAccount) {
                //send to create account page
                val intent = Intent(this, AddAccountActivity::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            }else{
                findViewById<CheckBox>(R.id.cbAccount).setChecked(true)
                Toast.makeText(this, "Account already set up", Toast.LENGTH_SHORT).show()
                txtSetUpAccount.isEnabled = false
            }
        }
        val txtSetUpBudget = findViewById<TextView>(R.id.txtSetUpBudget)
        txtSetUpBudget.setOnClickListener {
            if (!hasBudgetGoal) {
                //send user to Milestone page where they can set their monthly goal
                val intent = Intent(this, MilestoneActivity::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            }else{
                findViewById<CheckBox>(R.id.cbBudgetGoalSetup).setChecked(true)
                Toast.makeText(this, "Budget Goal already set up", Toast.LENGTH_SHORT).show()
                txtSetUpBudget.isEnabled = false
            }
        }
        val txtSetUpExCategory = findViewById<TextView>(R.id.txtSetUpExCategory)
        txtSetUpExCategory.setOnClickListener {
            if (!hasCategory) {
                //send to create expense category page
                val intent = Intent(this, CreateCategory::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            }else{
                findViewById<CheckBox>(R.id.cbCategorySetUp).setChecked(true)
                Toast.makeText(this, "Category already set up", Toast.LENGTH_SHORT).show()
                txtSetUpExCategory.isEnabled = false
            }
        }
    }
}