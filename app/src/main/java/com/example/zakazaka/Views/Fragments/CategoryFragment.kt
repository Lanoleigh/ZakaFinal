package com.example.zakazaka.Views.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zakazaka.Adapters.CategoryAdapter
import com.example.zakazaka.Data.Database.AppDatabase
import com.example.zakazaka.R
import com.example.zakazaka.Repository.AccountRepository
import com.example.zakazaka.Repository.BudgetGoalRepository
import com.example.zakazaka.Repository.CategoryRepository
import com.example.zakazaka.Repository.SubCategoryRepository
import com.example.zakazaka.Repository.TransactionRepository
import com.example.zakazaka.Repository.UserRepository
import com.example.zakazaka.ViewModels.CategoryViewModel
import com.example.zakazaka.ViewModels.ViewModelFactory
import com.example.zakazaka.Views.CategoryDetails
import com.example.zakazaka.Views.CreateCategory


class CategoryFragment : Fragment() {
    lateinit var categoryViewModel : CategoryViewModel
    lateinit var categoryAdapter : CategoryAdapter
    lateinit var categoryRecyclerView : RecyclerView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_category, container, false)


        val txtCategoryNav = view.findViewById<TextView>(R.id.createCategory)
        txtCategoryNav.setOnClickListener {
            val intent = Intent(requireContext(), CreateCategory::class.java)
            startActivity(intent)
        }

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext().applicationContext
        val factory = ViewModelFactory(
            UserRepository(AppDatabase.getDatabase(context).userDao()),
            AccountRepository(AppDatabase.getDatabase(context).accountDao()),
            BudgetGoalRepository(AppDatabase.getDatabase(context).budgetGoalDao()),
            CategoryRepository(AppDatabase.getDatabase(context).categoryDao()),
            SubCategoryRepository(AppDatabase.getDatabase(context).subCategoryDao()),
            TransactionRepository(AppDatabase.getDatabase(context).transactionDao())
        )

        val sharedPref = requireContext().getSharedPreferences("BudgetAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getLong("LOGGED_USER_ID", 0)

        categoryViewModel = ViewModelProvider(this, factory)[CategoryViewModel::class.java]
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView)
        categoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        categoryViewModel.getCategoriesByUserId(userId).observe(viewLifecycleOwner) { categories ->
            categoryAdapter = CategoryAdapter(categories){ category->
                val intent = Intent(requireContext(), CategoryDetails::class.java)
                intent.putExtra("categoryID", category.categoryID)
                startActivity(intent)
            }
            categoryRecyclerView.adapter = categoryAdapter

        }


    }

}