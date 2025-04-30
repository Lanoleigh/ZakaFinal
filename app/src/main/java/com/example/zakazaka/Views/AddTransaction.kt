package com.example.zakazaka.Views

import android.content.pm.PackageManager
import android.net.Uri
import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.zakazaka.Data.Database.AppDatabase
import com.example.zakazaka.Models.AccountEntity
import com.example.zakazaka.Models.SubCategoryEntity
import com.example.zakazaka.Models.TransactionEntity
import com.example.zakazaka.R
import com.example.zakazaka.Repository.AccountRepository
import com.example.zakazaka.Repository.BudgetGoalRepository
import com.example.zakazaka.Repository.CategoryRepository
import com.example.zakazaka.Repository.SubCategoryRepository
import com.example.zakazaka.Repository.TransactionRepository
import com.example.zakazaka.Repository.UserRepository
import com.example.zakazaka.ViewModels.AccountViewModel
import com.example.zakazaka.ViewModels.CategoryViewModel
import com.example.zakazaka.ViewModels.SubCategoryViewModel
import com.example.zakazaka.ViewModels.TransactionViewModel
import com.example.zakazaka.ViewModels.ViewModelFactory
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTransaction : AppCompatActivity() {
    var imageUri : Uri? = null
    lateinit var transactionViewModel : TransactionViewModel
    lateinit var categoryViewModel: CategoryViewModel
    lateinit var subCategoryViewModel: SubCategoryViewModel
    lateinit var accountViewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_transaction)
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
        val sharedPref = getSharedPreferences("BudgetAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getLong("LOGGED_USER_ID", 0)
        //initializing the ViewModels
        transactionViewModel = ViewModelProvider(this, factory)[TransactionViewModel::class.java]
        categoryViewModel = ViewModelProvider(this, factory)[CategoryViewModel::class.java]
        subCategoryViewModel = ViewModelProvider(this, factory)[SubCategoryViewModel::class.java]
        accountViewModel = ViewModelProvider(this, factory)[AccountViewModel::class.java]
        //getting the spinners
        val sAccSpiner = findViewById<Spinner>(R.id.sAccountName)
        val sCategorySpinner = findViewById<Spinner>(R.id.sCategorySpinner)
        val sSubCategorySpinner = findViewById<Spinner>(R.id.sSubCategorySpinner)
        //adding values to the spinners based of the users ID
        accountViewModel.getAccountsByUserId(userId).observe(this){ accounts ->
            val accNames = accounts.map { it.accountName }

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, accounts)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sAccSpiner.adapter = adapter
        }
        categoryViewModel.getCategoriesByUserId(userId).observe(this){ categories ->
            val catNames = categories.map{it.name}
            val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,catNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sCategorySpinner.adapter = adapter


            sCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                    val selectedCategory = categories[position]
                    val categoryId = selectedCategory.categoryID

                    subCategoryViewModel.getSubCategoriesForCategory(categoryId).observe(this@AddTransaction){ subCategories ->

                        val subAdapter = ArrayAdapter(this@AddTransaction,android.R.layout.simple_spinner_item,subCategories)
                        subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        sSubCategorySpinner.adapter = subAdapter
                    }

                }
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

        }
        val btnStartDate = findViewById<Button>(R.id.btnStartDate)
        val btnEndDate = findViewById<Button>(R.id.btnEndDatePicker)
        val startDatePicker = findViewById<DatePicker>(R.id.startDatePicker)
        val endDatePicker = findViewById<DatePicker>(R.id.endDateDatePicker)
        btnStartDate.setOnClickListener {
            startDatePicker.visibility = View.VISIBLE
            endDatePicker.visibility = View.GONE
        }
        btnEndDate.setOnClickListener {
            endDatePicker.visibility = View.VISIBLE
            startDatePicker.visibility = View.GONE
        }
        startDatePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            val selectedDate = "${monthOfYear + 1}/$dayOfMonth/$year"
            findViewById<EditText>(R.id.dateOfTransaction).setText(selectedDate)
        }
        endDatePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            val selectedDate = "${monthOfYear + 1}/$dayOfMonth/$year"
            findViewById<EditText>(R.id.dEndDate).setText(selectedDate)
        }
        findViewById<EditText>(R.id.edAmount).setOnClickListener {
            endDatePicker.visibility = View.GONE
        }

        try{
            val btnAddTransaction = findViewById<Button>(R.id.btnAddTransaction)
            btnAddTransaction.setOnClickListener {
                //adding a new transaction
                val amount = findViewById<android.widget.EditText>(R.id.edAmount).text.toString()
                val startDate = findViewById<EditText>(R.id.dateOfTransaction).text.toString()
                val endDate = findViewById<EditText>(R.id.dEndDate).text.toString()
                val description = findViewById<EditText>(R.id.edDescription).text.toString()
                if(description.isEmpty() || amount.isEmpty() || startDate.isEmpty() || endDate.isEmpty()
                    || sAccSpiner.selectedItem == null || sCategorySpinner.selectedItem == null || sSubCategorySpinner.selectedItem == null){
                    Toast.makeText(this,"Please fill in all fields",Toast.LENGTH_SHORT).show()
                }else{
                    val amt = amount.toDouble()
                    val date = startDate.toDate()
                    val endDate = endDate.toDate()

                    val selectedAccount = sAccSpiner.selectedItem as AccountEntity
                    val selectedSubCategory = sSubCategorySpinner.selectedItem as SubCategoryEntity


                    val transaction = TransactionEntity(
                        amount = amt,
                        date = date,//get from textbox
                        endDate = endDate,
                        repeat = "No",
                        description = description,
                        type = "Transaction",
                        currency = "ZAR",
                        subCategoryID = selectedSubCategory.subCategoryID,//get from spinner
                        accountID = selectedAccount.accountID,
                        imagePath = imageUri?.toString()//get from spinner
                    )
                    lifecycleScope.launch{
                        val transId = transactionViewModel.enterNewTransaction(transaction)
                            if(transId > 0) {
                                Toast.makeText(this@AddTransaction, "Transaction added successfully", Toast.LENGTH_SHORT).show()
                                subCategoryViewModel.updateSubCategoryCurrentAmount(selectedSubCategory.subCategoryID, amt)
                                //categoryViewModel.updateCategoryCurrentAmount() update the CategoryCurrent Amount
                                finish()
                            }else
                                Toast.makeText(this@AddTransaction,"Error adding transaction",Toast.LENGTH_SHORT).show()

                        }
                    }


                }
        }catch(e:Exception){
            Toast.makeText(this,"Error adding transaction",Toast.LENGTH_SHORT).show()
        }
        try{
            findViewById<Button>(R.id.btnUpload_receipt).setOnClickListener {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),100)
                }else{
                    launchCamera()
                }
            }
        }catch(e:Exception){
            Toast.makeText(this,"Error launching camera",Toast.LENGTH_SHORT).show()
        }



    }
    private fun String.toDate(): Date {
        val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return format.parse(this)?: Date()
    }
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { takePicure ->
        val uri = imageUri
        if(takePicure){
            if(takePicure && uri != null) {
                findViewById<ImageView>(R.id.imageView).setImageURI(imageUri)
            }
        }

    }
    private fun launchCamera(){
        val photo = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "photo.jpg")
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", photo)
        imageUri = uri
        takePictureLauncher.launch(uri)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            launchCamera()
        }else{
            Toast.makeText(this,"Camera Persmission is required",Toast.LENGTH_LONG).show()
        }
    }
}