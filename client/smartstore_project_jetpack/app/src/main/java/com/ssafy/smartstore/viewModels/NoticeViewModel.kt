package com.ssafy.smartstore.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.ssafy.smartstore.dto.Order
import com.ssafy.smartstore.repository.StoreRepository

private const val TAG = "NoticeViewModel_싸피"
class NoticeViewModel(private val repository: StoreRepository, application: Application) : AndroidViewModel(application) {
    private val list = mutableListOf<Order>()

    private var _noticeList = MutableLiveData<MutableList<Order>>()
    val noticeList : LiveData<MutableList<Order>>
        get() = _noticeList

    init {
        _noticeList.value = list
    }

    fun noticeInsert(notice: Order) {
        list.add(notice)
        _noticeList.value = list
    }

    fun noticeDelete(notice: Order) {
        Log.d(TAG, "noticeDelete: notice delete list: $list")
        list.remove(notice)
        Log.d(TAG, "noticeDelete: notice delete list: $list")
        _noticeList.value = list
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NoticeViewModel(StoreRepository.getInstance(application)!!, application) as T
        }
    }
}