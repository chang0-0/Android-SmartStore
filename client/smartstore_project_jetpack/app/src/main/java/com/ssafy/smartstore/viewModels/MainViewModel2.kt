package com.ssafy.smartstore.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.ssafy.smartstore.repository.StoreRepository
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse
import kotlinx.coroutines.launch

class MainViewModel2(private val repository: StoreRepository, productId: Int) : ViewModel() {

    private var _productInfo = MutableLiveData<List<MenuDetailWithCommentResponse>>()
    val productInfo : LiveData<List<MenuDetailWithCommentResponse>>
        get() = _productInfo

    init {
        viewModelScope.launch{
            _productInfo.value = repository.getComments(productId)
        }
    }

    class Factory(private val application: Application, val productId: Int): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel2(StoreRepository.getInstance(application)!!, productId) as T
        }
    }
}
