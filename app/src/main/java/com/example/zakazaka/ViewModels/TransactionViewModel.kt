package com.example.zakazaka.ViewModels


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zakazaka.Models.TransactionEntity
import com.example.zakazaka.Repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

class TransactionViewModel @Inject constructor(private val repository: TransactionRepository) : ViewModel() {
    //functionality to register a new transaction
    fun enterNewTransaction(transaction: TransactionEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTransaction(transaction)
        }
    }
    fun getAllTransactions(): LiveData<List<TransactionEntity>> {
        return repository.getAllTransctions
        //functionality to return a list of all transactions
    }
    fun deleteTransaction(transactionID:Long){
        //functionality to delete a transaction
    }
    //functionality to return a list of all transactions between two dates
    //this function will be called when user wants to see all transactions in a time period but filtered through the subcategory and main category as well.
    fun getTransactionsBetweenDates(startDate: Date, endDate: Date): LiveData<List<TransactionEntity>> {
        return repository.getTransactionsBySelectedPeriod(startDate,endDate)
    }
    //functionality to return a list of all transactions by a subcategory
    fun getTransactionsBySubCategory(subCategoryID:Long): LiveData<List<TransactionEntity>> {
        return repository.getTransactionsBySubCategory(subCategoryID)
    }
    //functionality to return a list of all transactions by an account
    fun getTransactionsByAccount(accountID:Long): LiveData<List<TransactionEntity>> {
        return repository.getTransactionsByAccount(accountID)

    }
    fun takeImage(transactionID: Long) {
        //functionality to take image and save it to the transaction
    }
}