package com.example.zakazaka.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zakazaka.Models.CategoryEntity
import com.example.zakazaka.R

class CategoryAdapter(
    private val categories: List<CategoryEntity>,
    private val onClick: (CategoryEntity) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryTitle: TextView = itemView.findViewById(R.id.categoryTitle1)
        val amountLeft: TextView = itemView.findViewById(R.id.amountLeft1)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]

        holder.categoryTitle.text = "Expense: ${category.name}"

        val amountLeft = category.budgetLimit - category.currentAmount
        holder.amountLeft.text = "R${String.format("%.2f", amountLeft)} left of R${String.format("%.2f", category.budgetLimit)}"

        val progress = ((category.currentAmount / category.budgetLimit) * 100).toInt()
        holder.progressBar.progress = progress

        holder.itemView.setOnClickListener {
            onClick(category)
        }
    }

    override fun getItemCount(): Int = categories.size
}
