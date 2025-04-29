package com.example.zakazaka.Repository


import androidx.lifecycle.LiveData
import com.example.zakazaka.Data.Database.UserDao
import com.example.zakazaka.Models.UserEntity


class UserRepository(private val userDao: UserDao) {
    //was thinking about CRUD operations here

    // getting all users from the database
    // LiveData will automatically update when data changes
    val readAllData: LiveData<List<UserEntity>> = userDao.readAllData()

    //registers a new user in the database
    suspend fun registerUser(user: UserEntity): Long {
        return userDao.insert(user)
    }

    //user logs in by username/email and password
    suspend fun loginUser(userNameOrEmail: String, password: String): UserEntity? {
        return userDao.login(userNameOrEmail, password)
    }

    //getting the user id
    suspend fun getUserById(userId: Long):UserEntity? {
        return userDao.getUserByID(userId)
    }

    //updates a user's information
    suspend fun updateUserDetails(user: UserEntity): Int {
        return userDao.update(user)
    }

    //when the user wants to delete their account by ID
    suspend fun deleteUserById(userId: Long): Int {
        return userDao.deleteUserbyID(userId)
    }

    // delete all users from the database
    suspend fun deleteAllUsers() {
        userDao.deleteAllUsers()
    }

}
