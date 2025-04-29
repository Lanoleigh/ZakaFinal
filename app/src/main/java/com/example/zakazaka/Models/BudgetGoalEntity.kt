package com.example.zakazaka.Models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Budget_Goal",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class,
            parentColumns = ["userID"],
            childColumns = ["userID"],
            onDelete = ForeignKey.CASCADE )
    ],

    indices = [Index(value = ["userID"])]
)
data class BudgetGoalEntity (
    @PrimaryKey(autoGenerate = true) val budgetGoalID : Long = 0,
    var minAmount : Double,
    var maxAmount : Double,
    var month : String,
    var status : String,
    var userID : Long

)