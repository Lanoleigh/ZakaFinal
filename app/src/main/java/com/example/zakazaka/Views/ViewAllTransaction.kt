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
import java.text.SimpleDateFormat
import java.util.Locale

class ViewAllTransaction : AppCompatActivity() {
    lateinit var transactionAdapter : TransactionAdapter
    lateinit var transactionViewModel : TransactionViewModel
    lateinit var transRecyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_all_transaction)
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
        transactionViewModel = ViewModelProvider(this,factory)[TransactionViewModel::class.java]
        transRecyclerView = findViewById(R.id.transactionsRecyclerView)
        transRecyclerView.layoutManager = LinearLayoutManager(this)

        transactionViewModel.getAllTransactions().observe(this){transactions ->
                transactionAdapter = TransactionAdapter(transactions){ transaction ->
                    val transactionId = transaction.transactionID
                    val intent = Intent(this, TransactionDetails::class.java)
                    intent.putExtra("TRANSACTION_ID", transactionId)
                    startActivity(intent)
                    //when the user clicks on a specific transaction they will be sent to a different page to view that transaction
                }
                transRecyclerView.adapter = transactionAdapter
        }
        val btnSort = findViewById<Button>(R.id.sortButton)

        btnSort.setOnClickListener{
            val startDate = findViewById<EditText>(R.id.startDate).text.toString()
            val endDate = findViewById<EditText>(R.id.endDate).text.toString()
            if(startDate.isEmpty() || endDate.isEmpty()){
                Toast.makeText(this,"Please fill in all fields", Toast.LENGTH_LONG).show()
            }else{
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                format.isLenient = false
                try {
                    val start = format.parse(startDate)
                    val end = format.parse(endDate)
                    if (start != null && end != null) {
                        transactionViewModel.getTransactionsBetweenDates(start,end).observe(this){ filteredTransactions ->
                            transactionAdapter = TransactionAdapter(filteredTransactions){}
                            transRecyclerView.adapter = transactionAdapter
                        }
                    }

                }catch(e:Exception)
                {//if the user enters an invalid date format they will be notified
                    Toast.makeText(this,"Invalid date format", Toast.LENGTH_LONG).show()
                }
            }
        }


    }
}