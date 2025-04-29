package com.example.zakazaka.Models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "Transaction",
    foreignKeys = [
        ForeignKey(entity = SubCategoryEntity::class,
            parentColumns = ["subCategoryID"],
            childColumns = ["subCategoryID"],
            onDelete = ForeignKey.CASCADE ),


        ForeignKey(entity = AccountEntity::class,
            parentColumns = ["accountID"],
            childColumns = ["accountID"],
            onDelete = ForeignKey.CASCADE )
    ],

    indices = [Index(value = ["subCategoryID"]), Index(value = ["accountID"])]
)
data class TransactionEntity (//Reference Module Manual
    @PrimaryKey(autoGenerate = true) val transactionID : Long = 0,
    var date : Date,
    var amount : Double,
    var repeat : String,
    var description : String,
    var type : String,
    var currency : String,
    var subCategoryID : Long,
    var accountID : Long
)