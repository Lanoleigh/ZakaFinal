package com.example.zakazaka.Repository

import androidx.lifecycle.LiveData
import com.example.zakazaka.Data.Database.TransactionDao
import com.example.zakazaka.Models.TransactionEntity
import java.util.Date

class TransactionRepository(private val transactionDao: TransactionDao) {

    // getting all the live transactions
    val getAllTransctions: LiveData<List<TransactionEntity>> = transactionDao.getAllTransactions()

    //getting transaction by id
    suspend fun getTransactionById(transactionId: Long): TransactionEntity? {
        return transactionDao.getTransactionById(transactionId)
    }

    //get transaction by date
    fun getTransactionsBySelectedPeriod(startDate: Date, endDate: Date): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionsByDate(startDate, endDate)
    }

    //get transaction by sub category
    fun getTransactionsBySubCategory(subCategoryId: Long): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionsBySubCategory(subCategoryId)
    }

    //get transaction by account
    fun getTransactionsByAccount(accountId: Long): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionsByAccount(accountId)
    }

    //get transaction by type
    fun getTransactionsByType(type: String): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionsByType(type)
    }

    //get transaction by date and subcategory
    fun getTransactionsByDateRangeAndSubCategory(startDate: Date, endDate: Date, subCategoryId: Long): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionsByDateAndSubCategory(startDate, endDate, subCategoryId)
    }

    /*get by date and account
    fun getTransactionsByDateRangeAndAccount(startDate: Date, endDate: Date, accountId: Long): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionsByDateRangeAndAccount(startDate, endDate, accountId)
    }*/

    //creating a new transaction
    suspend fun addTransaction(transaction: TransactionEntity): Long {
        return transactionDao.insertTransaction(transaction)
    }

    //updating a transaction
    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }

    //delete a transaction by ID
    suspend fun deleteTransaction(transactionId: Long) {
        transactionDao.deleteTransactionById(transactionId)
    }

    //deleting all transactions
    suspend fun deleteAllTransactions() {
        transactionDao.deleteAllTransactions()
    }




}