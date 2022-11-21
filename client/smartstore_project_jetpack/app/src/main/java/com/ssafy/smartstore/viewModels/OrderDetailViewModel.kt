package com.ssafy.smartstore.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.ssafy.smartstore.repository.StoreRepository
import com.ssafy.smartstore.response.OrderDetailResponse
import kotlinx.coroutines.launch

class OrderDetailViewModel(private val repository: StoreRepository, orderId: Int) : ViewModel(){
    private var _orderDetailList = MutableLiveData<List<OrderDetailResponse>>()
    val orderDetailList : LiveData<List<OrderDetailResponse>>
        get() = _orderDetailList

    init {
        viewModelScope.launch{
            val response = repository.getOrderDetail(orderId)
            if(response.isSuccessful) {
                _orderDetailList.value = response.body()
            }
        }
    }

    class Factory(private val application: Application, val orderId: Int): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OrderDetailViewModel(StoreRepository.getInstance(application)!!, orderId) as T
        }
    }
}