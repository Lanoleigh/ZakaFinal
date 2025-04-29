package com.example.zakazaka.Repository

import androidx.lifecycle.LiveData
import com.example.zakazaka.Data.Database.SubCategoryDao
import com.example.zakazaka.Models.SubCategoryEntity

class SubCategoryRepository(private val subCategoryDao: SubCategoryDao) {

    // Get all subcategories as LiveData
    val readAllData: LiveData<List<SubCategoryEntity>> = subCategoryDao.readAllData()

    // Insert a new subcategory
    suspend fun addSubCategory(subCategory: SubCategoryEntity): Long {
        return subCategoryDao.insert(subCategory)
    }

    // Get a specific subcategory by ID
    suspend fun getSubCategoryById(subCategoryId: Long): SubCategoryEntity? {
        return subCategoryDao.getSubCategoryById(subCategoryId)
    }

    // Get all subcategories for a specific category
    suspend fun getSubCategoriesForCategory(categoryId: Long): List<SubCategoryEntity> {
        return subCategoryDao.getSubCategoriesForCategory(categoryId)
    }

    // Update the budget limit for a subcategory
    suspend fun updateSubCategoryBudgetLimit(subCategoryId: Long, budgetLimit: Double): Int {
        return subCategoryDao.updateSubCategoryBudgetLimit(subCategoryId, budgetLimit)
    }

    // Update name and description for a subcategory
    suspend fun updateSubCategoryDetails(subCategoryId: Long, name: String, description: String): Int {
        return subCategoryDao.updateSubCategoryDetails(subCategoryId, name, description)
    }

    // Update the current amount for a subcategory
    suspend fun updateSubCategoryAmount(subCategoryId: Long, currentAmount: Double): Int {
        return subCategoryDao.updateSubCategoryAmount(subCategoryId, currentAmount)
    }

    // Delete all subcategories for a specific category
    suspend fun deleteSubCategoriesForCategory(categoryId: Long): Int {
        return subCategoryDao.deleteASubCategoriesForCategory(categoryId)
    }

    /*method takes in 3(parameters) values  and calls the method inside the DAO and passes the
    * variables to the method*/
    suspend fun transferBetweenSubCategories(
        fromSubCategoryId: Long,
        toSubCategoryId: Long,
        amount: Double
    ): SubCategoryDao.TransferResult {
        return subCategoryDao.transferBetweenSubCategories(
            fromSubCategoryId,
            toSubCategoryId,
            amount
        )
    }

    /*so the result parameter gets its value from the dao's transferBetweenSubCategories method,
    which returns one of the enum values from SubCategoryDao.TransferResult.*/
    fun getTransferResultMessage(result: SubCategoryDao.TransferResult): String {
        return when (result) {// "when" acts like a switch/case
            SubCategoryDao.TransferResult.SUCCESS ->
                "Transfer completed successfully"

            SubCategoryDao.TransferResult.SUCCESS_BUT_OVER_BUDGET ->
                "Transfer completed, but the destination subcategory now exceeds its budget limit"

            SubCategoryDao.TransferResult.ERROR_INSUFFICIENT_FUNDS ->
                "Error: Insufficient funds in the source subcategory"

            SubCategoryDao.TransferResult.ERROR_SUBCATEGORY_NOT_FOUND ->
                "Error: One or both subcategories not found"
        }
    }
}