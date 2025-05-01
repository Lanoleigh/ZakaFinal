package com.example.zakazaka.Data.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.zakazaka.Models.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TransactionDao {

    //UPDATES:

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM `Transaction` WHERE transactionID = :transactionId")
    suspend fun deleteTransactionById(transactionId: Long)

    @Query("DELETE FROM `Transaction`")
    suspend fun deleteAllTransactions()

    @Query("SELECT * FROM `Transaction` WHERE transactionID = :transactionId")
    suspend fun getTransactionById(transactionId: Long): TransactionEntity?

    @Query("SELECT * FROM `Transaction` ORDER BY date DESC")
    fun getAllTransactions(): LiveData<List<TransactionEntity>>

    @Query("SELECT * FROM `Transaction` WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDate(startDate: Date, endDate: Date): LiveData<List<TransactionEntity>>

    @Query("SELECT * FROM `Transaction` WHERE subCategoryID = :subCategoryId ORDER BY date DESC")
    fun getTransactionsBySubCategory(subCategoryId: Long): LiveData<List<TransactionEntity>>

    @Query("SELECT * FROM `Transaction` WHERE accountID = :accountId ORDER BY date DESC")
    fun getTransactionsByAccount(accountId: Long): LiveData<List<TransactionEntity>>

    @Query("""
        SELECT t.* FROM `Transaction` t
        INNER JOIN Sub_Category s ON t.subCategoryID = s.subCategoryID
        INNER JOIN Category c ON s.categoryID = c.categoryID
        WHERE c.userID = :userId
        ORDER BY t.date DESC
    """)
    fun getTransactionsForUser(userId: Long): LiveData<List<TransactionEntity>>
}