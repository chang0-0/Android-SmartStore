package com.ssafy.smartstore.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.ssafy.smartstore.dto.Product
import com.ssafy.smartstore.repository.StoreRepository
import kotlinx.coroutines.launch

private const val TAG = "OrderViewModel_싸피"
class ProductViewModel(private val repository: StoreRepository) : ViewModel(){
    private var _productList = MutableLiveData<List<Product>>()
    val productList : LiveData<List<Product>>
        get() = _productList

    init {
        getProductList()
    }

    fun getProductList() = viewModelScope.launch {
        val response = repository.getProductList()
        if(response.isSuccessful) {
            _productList.value = response.body()!!
        }
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProductViewModel(StoreRepository.getInstance(application)!!) as T
        }
    }
}