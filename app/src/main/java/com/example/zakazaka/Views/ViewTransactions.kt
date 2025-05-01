package com.example.zakazaka.Views

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zakazaka.Adapters.TransactionAdapter
import com.example.zakazaka.Data.Database.AppDatabase
import com.example.zakazaka.Models.CategoryEntity
import com.example.zakazaka.Models.TransactionEntity
import com.example.zakazaka.R
import com.example.zakazaka.Repository.AccountRepository
import com.example.zakazaka.Repository.BudgetGoalRepository
import com.example.zakazaka.Repository.CategoryRepository
import com.example.zakazaka.Repository.SubCategoryRepository
import com.example.zakazaka.Repository.TransactionRepository
import com.example.zakazaka.Repository.UserRepository
import com.example.zakazaka.ViewModels.CategoryViewModel
import com.example.zakazaka.ViewModels.TransactionViewModel
import com.example.zakazaka.ViewModels.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ViewTransactions : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var categoryViewModel: CategoryViewModel

    private lateinit var spinnerCategories: Spinner
    private lateinit var tvTotalAmount: TextView

    // Date filtering UI elements
    private lateinit var btnSelectStartDate: Button
    private lateinit var btnSelectEndDate: Button
    private lateinit var btnApplyFilter: Button
    private lateinit var btnClearFilter: Button
    private lateinit var tvDateRange: TextView

    private var startDate: Date? = null
    private var endDate: Date? = null
    private var userId: Long = 0
    private var selectedCategoryId: Long = -1 // -1 means "All Categories"
    private var categoryList: List<CategoryEntity> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_transactions)
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

        transactionViewModel = ViewModelProvider(this, factory)[TransactionViewModel::class.java]
        categoryViewModel = ViewModelProvider(this, factory)[CategoryViewModel::class.java]


        // Get logged in user ID
        val sharedPref = getSharedPreferences("BudgetAppPrefs", MODE_PRIVATE)
        userId = sharedPref.getLong("LOGGED_USER_ID", 0)

        if (userId.toInt() == 0) {
            // Handle case where user is not logged in
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        recyclerView = findViewById(R.id.transactionsRecyclerView)
        progressBar = findViewById(R.id.progressBar)


        //PLEASE CHANGE THIS TO BUTTONS, START AND END DATE BUTTONS
        btnSelectStartDate = findViewById(R.id.btnSelectStartDate)
        btnSelectEndDate = findViewById(R.id.btnSelectEndDate)
        btnApplyFilter = findViewById(R.id.sortButton)
        //PLEASE ADD A TEXT VIEW AS WELL
        tvDateRange = findViewById(R.id.tvDateRange)



        setupRecyclerView()
        setupDateFilteringUI()
        loadUserTransactions()
        observeTransactions()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(emptyList()) { transaction ->
            // Handle transaction click
            Toast.makeText(this, "Transaction: ${transaction.description}", Toast.LENGTH_SHORT).show()

            // Navigate to transaction details screen
            val intent = Intent(this, Dashboard::class.java).apply {
                putExtra("TRANSACTION_ID", transaction.transactionID)
            }
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = transactionAdapter
    }

    private fun setupDateFilteringUI() {
        // Format to display dates to users
        val displayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        // Start date picker
        btnSelectStartDate.setOnClickListener {
            showDatePicker { selectedDate ->
                startDate = selectedDate
                btnSelectStartDate.text = displayDateFormat.format(selectedDate)
                updateDateRangeDisplay()
            }
        }

        // End date picker
        btnSelectEndDate.setOnClickListener {
            showDatePicker { selectedDate ->
                endDate = selectedDate
                btnSelectEndDate.text = displayDateFormat.format(selectedDate)
                updateDateRangeDisplay()
            }
        }

        // Apply filter button
        btnApplyFilter.setOnClickListener {
            if (startDate != null && endDate != null) {
                if (startDate!!.after(endDate)) {
                    Toast.makeText(this, "Start date must be before end date", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                filterTransactionsByDateRange()
            } else {
                Toast.makeText(this, "Please select both start and end dates", Toast.LENGTH_SHORT).show()
            }
        }

        // Clear filter button
        btnClearFilter.setOnClickListener {
            clearDateFilter()
        }
    }

    private fun updateDateRangeDisplay() {
        if (startDate != null && endDate != null) {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val rangeText = "From: ${dateFormat.format(startDate!!)}\nTo: ${dateFormat.format(endDate!!)}"
            tvDateRange.text = rangeText
            tvDateRange.visibility = View.VISIBLE
            btnApplyFilter.isEnabled = true
        } else {
            tvDateRange.visibility = View.GONE
            btnApplyFilter.isEnabled = false
        }
    }

    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            onDateSelected(calendar.time)
        }

        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun filterTransactionsByDateRange() {
        if (startDate == null || endDate == null) return

        // Show loading state
        progressBar.visibility = View.VISIBLE

        // Get the filtered transactions
        val filteredTransactionsLiveData = transactionViewModel.getTransactionsBetweenDates(startDate!!, endDate!!)

        // Clear any previous observers to avoid duplicate updates
        if (this::filteredTransactionsObserver.isInitialized) {
            filteredTransactionsLiveData.removeObserver(filteredTransactionsObserver)
        }

        // Remove existing observers from userTransactions
        transactionViewModel.userTransactions.removeObservers(this)

        // Define the observer
        filteredTransactionsObserver = Observer<List<TransactionEntity>> { transactions ->
            progressBar.visibility = View.GONE

            if (transactions.isEmpty()) {
                recyclerView.visibility = View.GONE
                tvEmptyState.visibility = View.VISIBLE
                tvEmptyState.text = "No transactions found in the selected date range"
            } else {
                recyclerView.visibility = View.VISIBLE
                tvEmptyState.visibility = View.GONE
                transactionAdapter.updateTransactions(transactions)
            }
        }

        // Observe the filtered results
        filteredTransactionsLiveData.observe(this, filteredTransactionsObserver)
    }

    // Observer variable to maintain reference
    private lateinit var filteredTransactionsObserver: Observer<List<TransactionEntity>>

    private fun clearDateFilter() {
        // Reset date values
        startDate = null
        endDate = null

        // Reset UI elements
        btnSelectStartDate.text = "Select Start Date"
        btnSelectEndDate.text = "Select End Date"
        tvDateRange.visibility = View.GONE
        btnApplyFilter.isEnabled = false

        // Remove any existing filtered transactions observer
        if (this::filteredTransactionsObserver.isInitialized) {
            transactionViewModel.getTransactionsBetweenDates(Date(), Date()).removeObserver(filteredTransactionsObserver)
        }

        // Remove existing observers from userTransactions to avoid duplicates
        transactionViewModel.userTransactions.removeObservers(this)

        // Load all transactions again
        loadUserTransactions()
        observeTransactions()
    }

    private fun loadUserTransactions() {
        if (userId.toInt() != 0) {
            transactionViewModel.loadTransactionsForUser(userId)
        }
    }

    private fun observeTransactions() {
        // Observe loading state
        transactionViewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error messages
        transactionViewModel.errorMessage.observe(this) { errorMsg ->
            if (errorMsg != null) {
                tvEmptyState.visibility = View.VISIBLE
                tvEmptyState.text = errorMsg
                recyclerView.visibility = View.GONE
            } else {
                tvEmptyState.visibility = View.GONE
            }
        }

        // Observe transactions
        transactionViewModel.userTransactions.observe(this) { transactions ->
            if (transactions.isEmpty()) {
                recyclerView.visibility = View.GONE
                tvEmptyState.visibility = View.VISIBLE
                tvEmptyState.text = "No transactions found"
            } else {
                recyclerView.visibility = View.VISIBLE
                tvEmptyState.visibility = View.GONE
                transactionAdapter.updateTransactions(transactions)
            }
        }
    }
}