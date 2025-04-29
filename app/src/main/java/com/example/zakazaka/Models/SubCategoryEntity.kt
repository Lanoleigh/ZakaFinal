package com.example.zakazaka.Models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Sub_Category",
    foreignKeys = [

        ForeignKey(entity = CategoryEntity::class,
            parentColumns = ["categoryID"],
            childColumns = ["categoryID"],
            onDelete = ForeignKey.CASCADE )
    ],

    indices = [Index(value = ["categoryID"])]
)
data class SubCategoryEntity (//Reference Module Manual
    @PrimaryKey(autoGenerate = true) val subCategoryID : Long = 0,
    var name : String,
    var description : String,
    var budgetLimit : Double,
    var currentAmount : Double,
    var categoryID : Long
)