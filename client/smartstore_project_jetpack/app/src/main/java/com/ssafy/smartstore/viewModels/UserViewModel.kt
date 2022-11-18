package com.ssafy.smartstore.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.repository.UserRepository

private const val TAG = "UserViewModel_싸피"

// fragment -> viewModel -> repository -> api -> server(okhttp) -> api -> repository -> viewModel -> fragment
class UserViewModel(context : Context) : ViewModel() {
    private var userRepository : UserRepository

    private val _userInfo = MutableLiveData<User>().apply { 
        User() // 초기값
    }
    
    val userInfo: LiveData<User>
        get() = _userInfo

    init {
        Log.d(TAG, " @@@@@@@@@@@@@@@@@@@@@@@@ UserViewModel 실행. @@@@@@@@@@@@@@@@@@@@@@@@@@@@ : ")
        userRepository = UserRepository(context)
    }


    // LiveData를 관리하는 함수를 만드는거임
    // viewModel에서 repository를 호출한다.
    // viewModel 에서 호출하면, repository에 오고,

    fun getUserInfo(userId : String) {
        _userInfo.value = userRepository.getUserData(userId)
    }

}