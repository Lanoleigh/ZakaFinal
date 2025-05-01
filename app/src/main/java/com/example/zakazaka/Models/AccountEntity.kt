package com.example.zakazaka.Models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Account",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class,
            parentColumns = ["userID"],
            childColumns = ["userID"],
            onDelete = ForeignKey.CASCADE )
    ],

    indices = [Index(value = ["userID"])]
)
data class AccountEntity (
    @PrimaryKey(autoGenerate = true) val accountID : Long = 0,
    var accountName : String,
    var amount : Double,
    var type : String,
    var bankName : String,
    var userID : Long
){
    override fun toString():String{
        return accountName
    }
}
