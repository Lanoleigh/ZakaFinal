package com.example.zakazaka.Views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.zakazaka.Data.Database.AppDatabase
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
import com.example.zakazaka.ViewModels.LoginRegistrationViewModel
import com.example.zakazaka.ViewModels.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    lateinit var loginViewModel: LoginRegistrationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.transMain)) { v, insets ->
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
        val howtoViewModel = HowToViewModel()
        howtoViewModel.accountViewModel = AccountViewModel(AccountRepository(AppDatabase.getDatabase(this).accountDao()))
        howtoViewModel.budgetGoalViewModel = BudgetGoalViewModel(BudgetGoalRepository(AppDatabase.getDatabase(this).budgetGoalDao()))
        howtoViewModel.categoryViewModel = CategoryViewModel(CategoryRepository(AppDatabase.getDatabase(this).categoryDao()))



        loginViewModel = ViewModelProvider(this, factory)[LoginRegistrationViewModel::class.java]
        //user will create account
        val btnCreateAcc = findViewById<Button>(R.id.btnCreateAccount)
        btnCreateAcc.setOnClickListener {
            val intent = Intent(this, CreateAccount::class.java)//create this page
            startActivity(intent)
        }
        //functionality for the user to login
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            val email = findViewById<EditText>(R.id.edEmailUnameLogin).text.toString()
            val password = findViewById<EditText>(R.id.edPasswordLogin).text.toString()
            if(email.isEmpty() || password.isEmpty()){
                val toast = Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT)
                toast.show()
            } else{
                val user = loginViewModel.loginUser(email,password)
                user.observe(this){u ->
                    if(u != null){
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                        //this sends the user to the how to get started page if they have not completed the tutorial

                        howtoViewModel.isHowtoCompleted(u.userID){ completed ->
                            if(!completed) {
                                Toast.makeText(this, "Let's get you started", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, HowToGetStarted::class.java)
                                intent.putExtra("USER_ID", u.userID)
                                startActivity(intent)
                            } else{// if user completed the tutorial they will be sent to the dashboard page
                                Toast.makeText(this, "Welcome back ${u.firstName}", Toast.LENGTH_SHORT).show()

                                val sharedPref = getSharedPreferences("BudgetAppPrefs", MODE_PRIVATE)
                                with (sharedPref.edit()) {
                                    putLong("LOGGED_USER_ID", u.userID)
                                    apply()
                                }

                                val intent = Intent(this, Dashboard::class.java)
                                intent.putExtra("USER_ID", u.userID)
                                startActivity(intent)
                            }
                        }
                    }else{
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                    // remove observer after first response
                    user.removeObservers(this)
                }
            }


        }

    }
}