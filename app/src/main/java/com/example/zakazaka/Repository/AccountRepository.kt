package com.example.zakazaka.Repository

import androidx.lifecycle.LiveData
import com.example.zakazaka.Data.Database.AccountDao
import com.example.zakazaka.Models.AccountEntity


class AccountRepository(private val accountDao: AccountDao) {

    // getting all the accounts
    val getAllAccount: LiveData<List<AccountEntity>> = accountDao.getAllAccounts()

    //getting an account by ID
    suspend fun getAccountById(accountId: Long): AccountEntity? {
        return accountDao.getAccountById(accountId)
    }

    //get all the account connected to a specific user
    suspend fun getAccountsByUserId(userId: Long): List<AccountEntity> {
        return accountDao.getAccountsByUserId(userId)
    }

    //creating a new account
    suspend fun addAccount(account: AccountEntity) : Long {
       return accountDao.insertAccount(account)
    }

    //updating an account that already exists
    suspend fun updateAccount(account: AccountEntity) {
        accountDao.updateAccount(account)
    }

    //deleting an account by user ID
    suspend fun deleteAnAccount(userId: Long) {
        accountDao.deleteAccountById(userId)
    }

    /*deleting an account
    suspend fun deleteAccount(account: AccountEntity) {
        accountDao.deleteAccount(account)
    } */



}