package com.example.zakazaka.Models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Category",
    foreignKeys = [

        ForeignKey(entity = UserEntity::class,
            parentColumns = ["userID"],
            childColumns = ["userID"],
            onDelete = ForeignKey.CASCADE )
    ],

    indices = [Index(value = ["userID"])]
)
data class CategoryEntity(//Reference Module Manual
    @PrimaryKey(autoGenerate= true) val categoryID : Long = 0,
    var name : String,
    var budgetLimit : Double,
    var currentAmount : Double,
    var userID : Long
)