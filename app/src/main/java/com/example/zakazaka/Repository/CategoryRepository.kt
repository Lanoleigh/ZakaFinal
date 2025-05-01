package com.example.zakazaka.Repository

import androidx.lifecycle.LiveData
import com.example.zakazaka.Data.Database.CategoryDao
import com.example.zakazaka.Models.CategoryEntity


class CategoryRepository(private val categoryDao: CategoryDao) {

    // getting all users from the database
    // LiveData will automatically update when data changes
    val readAllData: LiveData<List<CategoryEntity>> = categoryDao.readAllData()

     fun getCategoryById(categoryId: Long): CategoryEntity? {
        return categoryDao.getCategoryById(categoryId)
    }

    //Inserts a new category into the database
    suspend fun createCategory(categoryEntity: CategoryEntity): Long {
        return categoryDao.insert(categoryEntity)
    }

    //getting all categories for a specific user
    suspend fun getCategory(userId: Long): List<CategoryEntity> {
        return categoryDao.getUserCategories(userId)
    }

    //getting the categories where the current amount goes over the budget limit
    suspend fun getOverBudgetCategory(userId: Long): List<CategoryEntity> {
        return categoryDao.getOverBudgetCategories(userId)
    }

    // updating data inside a category entity
    suspend fun updateCategory(category: CategoryEntity): Int {
        return categoryDao.updateCategory(category)
    }

    // updating the current amount for a specific category
    suspend fun updateCategoryCurrentAmount(categoryID: Long, amount: Double) {
        return categoryDao.updateCategoryAmount(categoryID, amount)
    }

    // updating the budget limit for a specific category
    suspend fun updateCategoryLimit(categoryID: Long, newLimit: Double): Int {
        return categoryDao.updateCategoryBudgetLimit(categoryID, newLimit)
    }

    // deleting a category by its ID
    suspend fun deleteCategory(categoryID: Long): Int {
        return categoryDao.deleteACategory(categoryID)
    }

    // delete all categories from the database
    suspend fun deleteAllCategories() {
        categoryDao.deleteAllCategories()
    }

}