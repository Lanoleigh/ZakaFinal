package com.example.zakazaka.Views

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.zakazaka.Data.Database.AppDatabase
import com.example.zakazaka.Models.CategoryEntity
import com.example.zakazaka.Models.SubCategoryEntity
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
import com.example.zakazaka.ViewModels.SubCategoryViewModel
import com.example.zakazaka.ViewModels.ViewModelFactory
import android.content.Context.MODE_PRIVATE


class CreateCategory : AppCompatActivity() {
    private var categoryId: Long = 0
    private var userId: Long = 0
    lateinit var categoryViewModel : CategoryViewModel
    lateinit var subCategoryViewModel: SubCategoryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_category)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.transMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve the user ID from SharedPreferences
        val sharedPref = getSharedPreferences("BudgetAppPrefs", MODE_PRIVATE)
        userId = sharedPref.getLong("LOGGED_USER_ID", 0)

        //initalizes the HowToView viewModel
        val howtoViewModel = HowToViewModel()
        howtoViewModel.accountViewModel =
            AccountViewModel(AccountRepository(AppDatabase.getDatabase(this).accountDao()))
        howtoViewModel.budgetGoalViewModel =
            BudgetGoalViewModel(BudgetGoalRepository(AppDatabase.getDatabase(this).budgetGoalDao()))
        howtoViewModel.categoryViewModel =
            CategoryViewModel(CategoryRepository(AppDatabase.getDatabase(this).categoryDao()))
        //initialize the ViewModelFactory
        val factory = ViewModelFactory(
            UserRepository(AppDatabase.getDatabase(this).userDao()),
            AccountRepository(AppDatabase.getDatabase(this).accountDao()),
            BudgetGoalRepository(AppDatabase.getDatabase(this).budgetGoalDao()),
            CategoryRepository(AppDatabase.getDatabase(this).categoryDao()),
            SubCategoryRepository(AppDatabase.getDatabase(this).subCategoryDao()),
            TransactionRepository(AppDatabase.getDatabase(this).transactionDao())
        )
        categoryViewModel = ViewModelProvider(this, factory)[CategoryViewModel::class.java]
        subCategoryViewModel = ViewModelProvider(this, factory)[SubCategoryViewModel::class.java]

        val btnCreateCategory = findViewById<Button>(R.id.btnCategory)
        val btnCreateSubCategory = findViewById<Button>(R.id.btnCreateSubCategory)


        //use the button click listener events to call the methods repsonsible for adding a new category
        btnCreateCategory.setOnClickListener {
            val edCategoryName = findViewById<EditText>(R.id.edCategoryName).text.toString()
            val edBudgetLimit = findViewById<EditText>(R.id.edCategoryLimit).text.toString()
            if (edCategoryName.isEmpty() || edBudgetLimit.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                setupCategoryCreation(edCategoryName, edBudgetLimit.toDouble())
            }
        }
        btnCreateSubCategory.setOnClickListener {
            val edSubCategoryName = findViewById<EditText>(R.id.edSubCategoryName).text.toString()
            val edSubCategoryBudgetLimit =
                findViewById<EditText>(R.id.edSubCategoryLimit).text.toString()
            //val edSubCategoryDescription = findViewById<EditText>(R.id.edDescription)
            if (edSubCategoryName.isEmpty() || edSubCategoryBudgetLimit.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                setupSubCategoryCreation(edSubCategoryName, edSubCategoryBudgetLimit.toDouble())
            }
        }
    }



    private fun setupCategoryCreation(categoryName:String, budgetLimit:Double) {

            try {
                // Create new category entity with the userId
                val newCategory = CategoryEntity(
                    name = categoryName,
                    budgetLimit = budgetLimit,
                    currentAmount = 0.0,
                    userID = userId  // Store the userID in the category table
                )

                // Save category to database
                val createdCategoryLiveData = categoryViewModel.createCategoryAndReturn(newCategory)

                createdCategoryLiveData.observe(this, ) { category ->
                    if (category != null) {
                        Toast.makeText(this, "Category created successfully", Toast.LENGTH_SHORT).show()

                        // Save the created category ID into SharedPreferences
                        val sharedPref = getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putLong("CREATED_CATEGORY_ID", category.categoryID)
                            apply()
                        }

                    } else {
                        Toast.makeText(this, "Failed to create category", Toast.LENGTH_SHORT).show()
                    }

                    // Very important: remove observer after getting the result
                    createdCategoryLiveData.removeObservers(this)
                }

            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter a valid budget limit", Toast.LENGTH_SHORT).show()
            }
        }


    private fun setupSubCategoryCreation(subCategoryName: String, budgetLimit: Double) {

            try {
                // To Retrieve the category ID from SharedPreferences
                val sharedPrefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE)
                val categoryId = sharedPrefs.getLong("CREATED_CATEGORY_ID", 0)


                // Create a new SubCategoryEntity
                val newSubCategory = SubCategoryEntity(
                    name = subCategoryName,
                    budgetLimit = budgetLimit,
                    description = "",
                    currentAmount = 0.0,
                    categoryID = categoryId // Link the subcategory to the parent category
                )

                // Save subcategory to database
                val createdSubCategoryLiveData = subCategoryViewModel.createSubCategory(newSubCategory)
                createdSubCategoryLiveData.observe(this,) { subCategory ->
                    if (subCategory != null) {
                        Toast.makeText(this, "Subcategory created successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to create subcategory", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter a valid budget limit", Toast.LENGTH_SHORT).show()
            }
        }
}

