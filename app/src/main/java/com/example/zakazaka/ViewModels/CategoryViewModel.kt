package com.example.zakazaka.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zakazaka.Models.CategoryEntity
import com.example.zakazaka.Repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoryViewModel @Inject constructor(private val repository: CategoryRepository) : ViewModel() {
    val categories : LiveData<List<CategoryEntity>> = repository.readAllData

    fun createCategoryAndReturn(category: CategoryEntity): LiveData<CategoryEntity?> {
        val result = MutableLiveData<CategoryEntity?>()
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.createCategory(category)
            if(id  > 0){
                val createdCategory = repository.getCategoryById(id)
                result.postValue(createdCategory)
            } else {
                result.postValue(null)
            }
        }
        return result
    }

    fun deleteCategory(categoryID:Long){
        //functionality to delete a category
    }
    fun updateCategoryLimit(categoryID:Long,newLimit:Double){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCategoryLimit(categoryID, newLimit)
        }
        //functionality to update the limit of a category, normallly when user deposits money from into an account
    }
    fun updateCategoryCurrentAmount(categoryID:Long,amount:Double){
        //functionality to Update the amount spent in a category, normally would be called in TransactionViewModel
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCategoryCurrentAmount(categoryID, amount)
        }
    }
     fun getCategoriesByUserId(userId:Long): LiveData<List<CategoryEntity>> {
        val categories = MutableLiveData<List<CategoryEntity>>()
        viewModelScope.launch(Dispatchers.IO) {
            categories.postValue(repository.getCategory(userId))
        }
        return categories
        //return repository.getCategory(userId)
    }

}