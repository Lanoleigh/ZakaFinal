package com.example.zakazaka.Views

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zakazaka.Adapters.SubCategoryAdapter
import com.example.zakazaka.Data.Database.AppDatabase
import com.example.zakazaka.Models.SubCategoryEntity
import com.example.zakazaka.R
import com.example.zakazaka.Repository.AccountRepository
import com.example.zakazaka.Repository.BudgetGoalRepository
import com.example.zakazaka.Repository.CategoryRepository
import com.example.zakazaka.Repository.SubCategoryRepository
import com.example.zakazaka.Repository.TransactionRepository
import com.example.zakazaka.Repository.UserRepository
import com.example.zakazaka.ViewModels.CategoryViewModel
import com.example.zakazaka.ViewModels.SubCategoryViewModel
import com.example.zakazaka.ViewModels.ViewModelFactory

class CategoryDetails : AppCompatActivity() {
    lateinit var subCategoryRecyclerView : RecyclerView
    lateinit var subCategoryAdapter : SubCategoryAdapter
    lateinit var categoryViewModel : CategoryViewModel
    lateinit var subCategoryViewModel : SubCategoryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category_details)
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
        val sharedPref = getSharedPreferences("BudgetAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getLong("LOGGED_USER_ID", 0)
        val categoryID = intent.getLongExtra("categoryID", 0)

        subCategoryViewModel = ViewModelProvider(this, factory)[SubCategoryViewModel::class.java]
        categoryViewModel = ViewModelProvider(this,factory)[CategoryViewModel::class.java]

        categoryViewModel.getCategorybyId(categoryID).observe(this){ category ->
            if(category != null){
                findViewById<TextView>(R.id.txtCategoryText).text = category.name
                findViewById<TextView>(R.id.txtCategoryBudget).text = "Budget : ${category.budgetLimit}"
                findViewById<TextView>(R.id.txtCategoryCurrentAmt).text = "Current : ${category.currentAmount}"
                findViewById<TextView>(R.id.txtRemainingBalance).text = "Remaining Balance : ${category.budgetLimit - category.currentAmount}"
            }
        }
        subCategoryRecyclerView = findViewById(R.id.subcategoryRecyclerView)
        subCategoryRecyclerView.layoutManager = LinearLayoutManager(this)

        subCategoryViewModel.getSubCategoriesForCategory(categoryID).observe(this){ subCategories ->
            subCategoryAdapter = SubCategoryAdapter(subCategories){}
            subCategoryRecyclerView.adapter = subCategoryAdapter
        }

        //making add subcategory button visible
        val btnAddSubCategory = findViewById<Button>(R.id.btnAddSubCategory)
        btnAddSubCategory.setOnClickListener{
            findViewById<EditText>(R.id.edAddsubCatName).visibility = View.VISIBLE
            findViewById<EditText>(R.id.edAddsubCatBudget).visibility = View.VISIBLE
            findViewById<Button>(R.id.btnSaveSubCategory).visibility = View.VISIBLE

        }
        //saving the sub category to database.
        val btnSaveSubCategory = findViewById<Button>(R.id.btnSaveSubCategory)
        btnSaveSubCategory.setOnClickListener {
            val edAddsubCatName = findViewById<EditText>(R.id.edAddsubCatName)
            val edAddsubCatBudget = findViewById<EditText>(R.id.edAddsubCatBudget)
            if(edAddsubCatName.text.isNotEmpty() && edAddsubCatBudget.text.isNotEmpty()){
                val subcategory = SubCategoryEntity(
                    name = edAddsubCatName.text.toString(),
                    budgetLimit = edAddsubCatBudget.text.toString().toDouble(),
                    currentAmount = 0.0,
                    description = "",
                    categoryID = categoryID
                )
                subCategoryViewModel.createSubCategory(subcategory).observe(this){ cat ->
                    if(cat != null){
                        Toast.makeText(this,"SubCategory Successfull created", Toast.LENGTH_SHORT).show()
                        edAddsubCatName.text.clear()
                        edAddsubCatBudget.text.clear()
                    }
                }
            }else{
                Toast.makeText(this,"Please fill in all fields",Toast.LENGTH_LONG).show()
            }


        }

    }
}