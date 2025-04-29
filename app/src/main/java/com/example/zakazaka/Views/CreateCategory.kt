package com.example.zakazaka.Views

import android.content.Context
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
        howtoViewModel.accountViewModel = AccountViewModel(AccountRepository(AppDatabase.getDatabase(this).accountDao()))
        howtoViewModel.budgetGoalViewModel = BudgetGoalViewModel(BudgetGoalRepository(AppDatabase.getDatabase(this).budgetGoalDao()))
        howtoViewModel.categoryViewModel = CategoryViewModel(CategoryRepository(AppDatabase.getDatabase(this).categoryDao()))
        //initialize the ViewModelFactory
        val factory = ViewModelFactory(
            UserRepository(AppDatabase.getDatabase(this).userDao()),
            AccountRepository(AppDatabase.getDatabase(this).accountDao()),
            BudgetGoalRepository(AppDatabase.getDatabase(this).budgetGoalDao()),
            CategoryRepository(AppDatabase.getDatabase(this).categoryDao()),
            SubCategoryRepository(AppDatabase.getDatabase(this).subCategoryDao()),
            TransactionRepository(AppDatabase.getDatabase(this).transactionDao())
        )
        val categoryViewModel = ViewModelProvider(this,factory)[CategoryViewModel::class.java]
        val subCategoryViewModel = ViewModelProvider(this,factory)[SubCategoryViewModel::class.java]

        val btnCreateCategory = findViewById<Button>(R.id.btnCategory)
        val btnCreateSubCategory = findViewById<Button>(R.id.btnCreateSubCategory)




        // To Retrieve the category ID from SharedPreferences
        val sharedPrefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE)
        categoryId = sharedPrefs.getLong("CREATED_CATEGORY_ID", 0)



        // Check if we got a valid user ID
        if (userId == 0L) {
            // Try to get it from intent as fallback
            userId = intent.getLongExtra("USER_ID", 0)

            if (userId == 0L) {
                Toast.makeText(this, "Error: User ID not found", Toast.LENGTH_SHORT).show()
                finish() // Close the activity if no user ID is found
                return
            }
        }

        // If not found, try get from Intent
        if (categoryId == 0L) {
            categoryId = intent.getLongExtra("CATEGORY_ID", 0)
            if (categoryId == 0L) {
                Toast.makeText(this, "Error: Category ID not found", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
        }



        // Now call the method to set up category creation
        setupCategoryCreation()

        // Now call method to set up subcategory creation
        setupSubCategoryCreation()

        // Optional: Display user ID for debugging
        // Toast.makeText(this, "User ID: $userId", Toast.LENGTH_SHORT).show()
    }

    private fun setupCategoryCreation() {
        val edCategoryName = findViewById<EditText>(R.id.edCategoryName)
        val edBudgetLimit = findViewById<EditText>(R.id.edCategoryLimit)
        val btnCreateCategory = findViewById<Button>(R.id.btnCategory)

        btnCreateCategory.setOnClickListener {
            val categoryName = edCategoryName.text.toString()
            val budgetLimitText = edBudgetLimit.text.toString()

            if (categoryName.isEmpty() || budgetLimitText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val budgetLimit = budgetLimitText.toDouble()

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

                        // Clear input fields
                        edCategoryName.text.clear()
                        edBudgetLimit.text.clear()


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
    }

    private fun setupSubCategoryCreation() {
        val edSubCategoryName = findViewById<EditText>(R.id.edSubCategoryName)
        val edSubCategoryBudgetLimit = findViewById<EditText>(R.id.edSubCategoryLimit)
        //val edSubCategoryDescription = findViewById<EditText>(R.id.edDescription)
        val btnCreateSubCategory = findViewById<Button>(R.id.btnCreateSubCategory)

        btnCreateSubCategory.setOnClickListener {
            val subCategoryName = edSubCategoryName.text.toString()
            val budgetLimitText = edSubCategoryBudgetLimit.text.toString()
            //val description = edSubCategoryDescription.text.toString()

            if (subCategoryName.isEmpty() || budgetLimitText.isEmpty()) {
                Toast.makeText(this, "Please fill in the subcategory name and budget limit", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val budgetLimit = budgetLimitText.toDouble()

                // Create a new SubCategoryEntity
                val newSubCategory = SubCategoryEntity(
                    name = subCategoryName,
                    budgetLimit = budgetLimit,
                    description = "",
                    currentAmount = 0.0,
                    categoryID = categoryId // Link the subcategory to the parent category
                )

                // Save subcategory to database
                subCategoryViewModel.createSubCategory(newSubCategory)
                Toast.makeText(this, "Subcategory created successfully", Toast.LENGTH_SHORT).show()

                // Clear input fields
                edSubCategoryName.text.clear()
                edSubCategoryBudgetLimit.text.clear()
               // edSubCategoryDescription.text.clear()

                // (Optional) Navigate back to previous screen
                // finish()

            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter a valid budget limit", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
