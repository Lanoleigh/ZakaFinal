package com.example.zakazaka.Repository

import androidx.lifecycle.LiveData
import com.example.zakazaka.Data.Database.BudgetGoalDao
import com.example.zakazaka.Models.BudgetGoalEntity


class BudgetGoalRepository(private val budgetGoalDao: BudgetGoalDao) {
    //nia please add the function where the user has to add a minimum and maximum budget goal
    // Directly expose all budget goals as a property
    val readAllBudgetGoals: LiveData<List<BudgetGoalEntity>> = budgetGoalDao.getAllBudgetGoals()

    //get the budget goals for a for a specific user
    suspend fun getBudgetGoalsByUserId(userId: Long): List<BudgetGoalEntity> {
        return budgetGoalDao.getBudgetGoalsByUserId(userId)
    }


    /*
    suspend fun getBudgetGoalById(budgetGoalId: Long): BudgetGoalEntity? {
        return budgetGoalDao.getBudgetGoalById(budgetGoalId)
    }*/


    //viewing the budget goal of user by month
    fun getBudgetGoalsByUserIdAndMonth(userId: Long, month: String): LiveData<List<BudgetGoalEntity>> {
        return budgetGoalDao.getBudgetGoalsByUserIdAndMonth(userId, month)
    }

    //viewing the budget goal of user by status
    fun getBudgetGoalsByUserIdAndStatus(userId: Long, status: String): LiveData<List<BudgetGoalEntity>> {
        return budgetGoalDao.getBudgetGoalsByUserIdAndStatus(userId, status)
    }

    //creating a new budget goal
    suspend fun addBudgetGoal(budgetGoal: BudgetGoalEntity): Long {
        return budgetGoalDao.insertBudgetGoal(budgetGoal)
    }

    //updating a budget goal
    suspend fun updateBudgetGoal(budgetGoal: BudgetGoalEntity) {
        budgetGoalDao.updateBudgetGoal(budgetGoal)
    }

    //deleting a budget goal by ID
    suspend fun deleteBudgetGoal(budgetGoalId: Long) {
        budgetGoalDao.deleteBudgetGoalById(budgetGoalId)
    }

    //deleting all budget goals
    suspend fun deleteAllBudgetGoals() {
        budgetGoalDao.deleteAllBudgetGoals()
    }

    //updating budget goal status
    suspend fun updateBudgetGoalStatus(budgetGoalId: Long, status: String) {
        budgetGoalDao.updateBudgetGoalStatus(budgetGoalId, status)
    }
}