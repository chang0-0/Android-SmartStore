package com.ssafy.smartstore.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.ssafy.smartstore.dto.Order
import com.ssafy.smartstore.repository.StoreRepository
import com.ssafy.smartstore.response.LatestOrderResponse
import kotlinx.coroutines.launch

private const val TAG = "OrderViewModel_싸피"
class OrderViewModel(private val repository: StoreRepository, userId: String) : ViewModel(){
    private var _lastMonthOrderList = MutableLiveData<List<LatestOrderResponse>>()
    val lastMonthOrderList : LiveData<List<LatestOrderResponse>>
        get() = _lastMonthOrderList

    var orderId = -1

    init {
        getLatesetOrderList(userId)
    }

    fun makeOrder(order: Order) = viewModelScope.launch {
        val response = repository.makeOrder(order)
        if(response.isSuccessful) {
            orderId = response.body()!!
        }
    }

    fun getLatesetOrderList(userId: String) = viewModelScope.launch {
        val response = repository.getLastMonthOrder(userId)
        if(response.isSuccessful) {
            _lastMonthOrderList.value = makeLatestOrderList(response.body()!!)
        }
    }

    private fun makeLatestOrderList(latestOrderList: List<LatestOrderResponse>): List<LatestOrderResponse> {
        val hm = HashMap<Int, LatestOrderResponse>()
        latestOrderList.forEach { order ->
            if (hm.containsKey(order.orderId)) {
                val tmp = hm[order.orderId]!!
                tmp.orderCnt += order.orderCnt
                tmp.totalPrice += order.productPrice * order.orderCnt
                hm[order.orderId] = tmp
            } else {
                order.totalPrice = order.productPrice * order.orderCnt
                hm[order.orderId] = order
            }
        }
        val list = ArrayList<LatestOrderResponse>(hm.values)
        list.sortWith { o1, o2 -> o2.orderDate.compareTo(o1.orderDate) }
        return list
    }

    class Factory(private val application: Application, val userId: String): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OrderViewModel(StoreRepository.getInstance(application)!!, userId) as T
        }
    }
}