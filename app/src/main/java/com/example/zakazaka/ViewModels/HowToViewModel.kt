package com.example.zakazaka.ViewModels

import androidx.lifecycle.MutableLiveData
import com.example.zakazaka.Models.AccountEntity
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.Observer
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class HowToViewModel() : ViewModel() {
    lateinit var budgetGoalViewModel: BudgetGoalViewModel
    lateinit var accountViewModel: AccountViewModel
    lateinit var categoryViewModel: CategoryViewModel


    //main function that handles is called when the user logs in.
    fun isHowtoCompleted(userId: Long,lifecycleOwner: LifecycleOwner , callback:(Boolean) -> Unit){
        checkForAccount(userId,lifecycleOwner) { hasAccount ->
            checkForCategory(userId,lifecycleOwner) { hasCategory ->
                checkForBudgetGoal(userId,lifecycleOwner) { hasBudgetGoal ->
                    val completed = hasAccount && hasCategory && hasBudgetGoal
                    callback(completed)
                }
            }
        }
    }
    fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>){
        observe(owner, object : Observer<T> {
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
    fun checkForAccount(uId: Long,lifecycleOwner: LifecycleOwner ,callback: (Boolean) -> Unit) {
        accountViewModel.getAccountsByUserId(uId).observeOnce(lifecycleOwner) { accounts ->
            callback(!accounts.isNullOrEmpty())
        }
    }
     fun checkForCategory(uId: Long,lifecycleOwner: LifecycleOwner , callback: (Boolean) -> Unit) {
      categoryViewModel.getCategoriesByUserId(uId).observeOnce(lifecycleOwner){ categories ->
          callback(!categories.isNullOrEmpty())
      }
    }
     fun checkForBudgetGoal(uId: Long ,lifecycleOwner: LifecycleOwner , callback: (Boolean) -> Unit) {
        budgetGoalViewModel.getBudgetGoal(uId).observeOnce(lifecycleOwner){ budgetGoals ->
            callback(!budgetGoals.isNullOrEmpty())

        }
    }

}