package com.example.zakazaka.Views

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
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
import com.example.zakazaka.ViewModels.CategoryViewModel
import com.example.zakazaka.ViewModels.SubCategoryViewModel
import com.example.zakazaka.ViewModels.TransactionViewModel
import com.example.zakazaka.ViewModels.ViewModelFactory

class TransactionDetails : AppCompatActivity() {
    lateinit var transactionViewModel : TransactionViewModel
    lateinit var subCategoryViewModel : SubCategoryViewModel
    lateinit var categoryViewModel : CategoryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaction_details)
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
        val transactionId = intent.getLongExtra("TRANSACTION_ID", -1)
        transactionViewModel = ViewModelProvider(this,factory)[TransactionViewModel::class.java]
        subCategoryViewModel = ViewModelProvider(this,factory)[SubCategoryViewModel::class.java]
        categoryViewModel = ViewModelProvider(this,factory)[CategoryViewModel::class.java]
        var categoryString :String = ""
        transactionViewModel.getTransactionById(transactionId).observe(this){ transaction->
            findViewById<TextView>(R.id.txtTransDescription).text = transaction.description
            findViewById<TextView>(R.id.txtTransAmount).text = transaction.amount.toString()
            findViewById<TextView>(R.id.txtDateOfTransaction).text = transaction.date.toString()
            findViewById<TextView>(R.id.txtType).text = transaction.type
           subCategoryViewModel.getSubCategorybyId(transaction.subCategoryID).observe(this){subCategory->
               val subCatName = subCategory.name
               val catId = subCategory.categoryID
               categoryViewModel.getCategorybyId(catId).observe(this){category->
                   if(category != null)
                    categoryString = "${category.name} -> ${subCatName}"
               }
           }
            findViewById<TextView>(R.id.txtCategory).text = categoryString
            findViewById<TextView>(R.id.txtRecurring).text = transaction.repeat
            if(transaction.imagePath != null){
                findViewById<ImageView>(R.id.imgReceipt).setImageURI(Uri.parse(transaction.imagePath))
            }
        }

    }
}