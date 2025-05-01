package com.example.zakazaka.ViewModels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zakazaka.Models.TransactionEntity
import com.example.zakazaka.Repository.CategoryRepository
import com.example.zakazaka.Repository.SubCategoryRepository
import com.example.zakazaka.Repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class TransactionViewModel @Inject constructor(private val repository: TransactionRepository
) : ViewModel() {

    //UPDATED CODE
    // Loading and Error State
    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> get() = _errorMessage

    // LiveData for transactions
    val userTransactions = MediatorLiveData<List<TransactionEntity>>()

    fun loadTransactionsForUser(userId: Long) {
        _isLoading.value = true
        userTransactions.value = emptyList()

        val source = repository.getTransactionById(userId)
        userTransactions.addSource(source) { transactions ->
            _isLoading.value = false
            userTransactions.value = transactions
        }
    }

    suspend fun enterNewTransaction(transaction: TransactionEntity): Long {
        return repository.addTransaction(transaction)
    }



    fun getTransactionsBetweenDates(startDate: Date, endDate: Date): LiveData<List<TransactionEntity>> {
        return repository.getTransactionsBySelectedPeriod(startDate, endDate)
    }

    fun getTransactionsBySubCategory(subCategoryId: Long): LiveData<List<TransactionEntity>> {
        return repository.getTransactionsBySubCategory(subCategoryId)
    }

    fun getTransactionsByAccount(accountId: Long): LiveData<List<TransactionEntity>> {
        return repository.getTransactionsByAccount(accountId)
    }

    fun takeImage(transactionId: Long) {
        // Implement image capturing and saving here
    }
}