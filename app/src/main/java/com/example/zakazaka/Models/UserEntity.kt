package com.example.zakazaka.Models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "User")
data class UserEntity (
    @PrimaryKey(autoGenerate = true) val userID: Long = 0,
    var username : String,
    var firstName : String,
    var lastName : String,
    var email : String,
    var password : String
)//(Adhiguna, 2021)
