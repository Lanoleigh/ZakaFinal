package com.example.zakazaka.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zakazaka.Models.SubCategoryEntity
import com.example.zakazaka.Repository.SubCategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SubCategoryViewModel @Inject constructor(private val repository: SubCategoryRepository) : ViewModel() {
    fun createSubCategory(subCategory: SubCategoryEntity): LiveData<Long>{
        //functionality to create a subcategory
        val subCategoryID = MutableLiveData<Long>()
        viewModelScope.launch(Dispatchers.IO){
            subCategoryID.postValue(repository.addSubCategory(subCategory) )
        }
        return subCategoryID
    }
    fun getSubCategories(): LiveData<List<SubCategoryEntity>> {
        //functionality to return a list of all subcategories
        return repository.readAllData
    }
    fun deleteSubCategory(categoryID:Long){
        //functionality to delete a subcategory
    }
    fun updateSubCategoryLimit(subCategoryID: Long,newLimit:Double){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSubCategoryBudgetLimit(subCategoryID, newLimit)
        }
    }
    fun updateSubCategoryCurrentAmount(subCategoryID: Long,amount:Double){
        //functionality to Update the amount spent in a subcategory, called when a transaction is made
        viewModelScope.launch(Dispatchers.IO){
            repository.updateSubCategoryAmount(subCategoryID,amount)
        }
    }
    fun transferBetweenCategories(fromCategoryID:Long,toCategoryID:Long,amount:Double){
        //functionality to transfer money between categories
        viewModelScope.launch(Dispatchers.IO) {
            repository.transferBetweenSubCategories(fromCategoryID, toCategoryID, amount)
        }

    }
}