package com.example.zakazaka.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zakazaka.Models.BudgetGoalEntity
import com.example.zakazaka.Repository.BudgetGoalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class BudgetGoalViewModel @Inject constructor(private val repository: BudgetGoalRepository) :
    ViewModel() {
    suspend fun setMonthlyGoal(amount:Double){
    }
    fun getUserStatus():String{
        return ""
    }
    suspend fun updateUserStatus(currentStatus:String){
    }
    suspend fun setMinimumGoal(amount:Double){

    }
    suspend fun getBudgetGoal(userId:Long): List<BudgetGoalEntity> {
        return  repository.getBudgetGoalsByUserId(userId)
    }
    fun addBudgetGoal(budgetGoal: BudgetGoalEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addBudgetGoal(budgetGoal)
        }

    }
    fun updateBudgetGoal(budgetGoal: BudgetGoalEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateBudgetGoal(budgetGoal)
        }
    }

}