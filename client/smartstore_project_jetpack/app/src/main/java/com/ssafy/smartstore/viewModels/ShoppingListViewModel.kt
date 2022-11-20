package com.ssafy.smartstore.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.ssafy.smartstore.dto.OrderDetail
import com.ssafy.smartstore.repository.StoreRepository
import kotlinx.coroutines.launch

class ShoppingListViewModel(private val repository: StoreRepository) : ViewModel(){
    private val list = mutableListOf<OrderDetail>()
    private var _shoppingList = MutableLiveData<MutableList<OrderDetail>>()
    val shoppingList : LiveData<MutableList<OrderDetail>>
        get() = _shoppingList

    init {
        _shoppingList.value = list
    }

    fun shoppingListInsert(orderDetail: OrderDetail) {
        var isAlreadyInserted = false

        for(i: Int in 0 until list.size) {
            // 이미 장바구니에 담긴 상품이라면 해당 상품의 개수 증가
            if(list[i].productId == orderDetail.productId) {
                list[i].quantity += orderDetail.quantity
                isAlreadyInserted = true
                break
            }
        }
        // 장바구니에 없던 상품이라면 리스트에 추가
        if(!isAlreadyInserted) {
            list.add(orderDetail)
        }

        _shoppingList.value = list
    }

    fun shoppingListDelete(orderDetail: OrderDetail) = viewModelScope.launch{
        list.remove(orderDetail)
        _shoppingList.value = list
    }

    fun shoppingListClear() {
        list.clear()
        _shoppingList.value = list
    }

    fun shoppingListInitWithLatestOrder(orderId: Int) = viewModelScope.launch {
        for(order in repository.getOrderInfo(orderId).body()!!) {
            val orderDetail = OrderDetail(productId = order.productId, quantity = order.quantity)
            orderDetail.img = order.img
            orderDetail.productName = order.productName
            orderDetail.unitPrice = order.unitPrice

            list.add(orderDetail)
        }
        _shoppingList.value = list
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ShoppingListViewModel(StoreRepository.getInstance(application)!!) as T
        }
    }
}