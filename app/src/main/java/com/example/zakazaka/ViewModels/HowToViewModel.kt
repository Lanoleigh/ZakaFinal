package com.example.zakazaka.ViewModels

import androidx.lifecycle.MutableLiveData
import com.example.zakazaka.Models.AccountEntity
import kotlinx.coroutines.Dispatchers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HowToViewModel() : ViewModel() {
    lateinit var budgetGoalViewModel: BudgetGoalViewModel
    lateinit var accountViewModel: AccountViewModel
    lateinit var categoryViewModel: CategoryViewModel



    //main function that handles is called when the user logs in.
    fun isHowtoCompleted(userId: Long, callback:(Boolean) -> Unit){
         viewModelScope.launch{
             val completed = checkForAccount(userId) && checkForCategory(userId) && checkForBudgetGoal(userId)
            callback(completed)
         }//if any one of these return false then the tutorial has not been completed
    }

    suspend fun checkForAccount(uId: Long): Boolean {
     //return accountViewModel.getAccountsByUserId(uId).isNotEmpty() ?: false
    return true
    }

    suspend fun checkForCategory(uId: Long): Boolean {
        //return categoryViewModel.getCategoriesByUserId(uId).isNotEmpty() ?: false
        return true
    }

    suspend fun checkForBudgetGoal(uId: Long):Boolean {
        //return budgetGoalViewModel.getBudgetGoal(uId).isNotEmpty() ?: false
        return true
    }

}