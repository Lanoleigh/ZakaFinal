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
    fun getSubCategoriesForCategory(categoryID:Long):LiveData<List<SubCategoryEntity>>{
        val subCategories = MutableLiveData<List<SubCategoryEntity>>()
        viewModelScope.launch(Dispatchers.IO){
            subCategories.postValue(repository.getSubCategoriesForCategory(categoryID))
        }
        return subCategories

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
    fun getSubCategorybyId(subCategoryID:Long):LiveData<SubCategoryEntity>{
        val subcategory = MutableLiveData<SubCategoryEntity>()
        viewModelScope.launch(Dispatchers.IO){
            subcategory.postValue(repository.getSubCategoryById(subCategoryID))
        }
        return subcategory
    }
    fun updateSubCategoryCurrentAmount(subCategoryID: Long,amount:Double){
        var newAmount : Double = amount
        //functionality to Update the amount spent in a subcategory, called when a transaction is made
        viewModelScope.launch(Dispatchers.IO){
            val subcategory = repository.getSubCategoryById(subCategoryID)
            if(subcategory != null) {
                newAmount = subcategory.currentAmount + amount
            }
            repository.updateSubCategoryAmount(subCategoryID,newAmount)
        }
    }
    fun transferBetweenCategories(fromCategoryID:Long,toCategoryID:Long,amount:Double){
        //functionality to transfer money between categories
        viewModelScope.launch(Dispatchers.IO) {
            repository.transferBetweenSubCategories(fromCategoryID, toCategoryID, amount)
        }

    }
}