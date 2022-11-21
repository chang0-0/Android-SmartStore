package com.ssafy.smartstore.viewModels

import Stamp
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.ssafy.smartstore.dto.Grade
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.repository.StoreRepository
import com.ssafy.smartstore.repository.UserRepository
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "UserViewModel_싸피"

// fragment -> viewModel -> repository -> api -> server(okhttp) -> api -> repository -> viewModel -> fragment
class UserViewModel(private val userRepository: UserRepository, userId: String) : ViewModel() {

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User>
        get() = _userData

    private val _userGrade = MutableLiveData<Grade>()
    val userGrade: LiveData<Grade>
        get() = _userGrade

    init{
        viewModelScope.launch {
            getUserData(userId)
        }
    }
    // LiveData를 관리하는 함수를 만드는거임
    // viewModel에서 repository를 호출한다.
    // viewModel 에서 호출하면, repository에 오고,

    // 사용자 데이터 가져오기
    suspend fun getUserData(userId: String) = viewModelScope.launch {
        val response = userRepository.getUserData(userId)

        Log.d(TAG, "ViewModel getUserData:${response} ")
        Log.d(TAG, "ViewModel getUserData:${response.body()} ")

        if (response.isSuccessful) {
//            val result = response.body()
            val userInfoResponse = response.body()
            val result = userInfoResponse!!.user
            val grade = userInfoResponse!!.grade

            _userGrade.value = grade
            Log.d(TAG, "getUserData: grade: $grade")



            _userData.value = result
            Log.d(TAG, "getUserData: $result ")

            withContext(Main) {
                if (result != null) {
                    Log.d(TAG, "getUserData: ${result}")
                    _userData.value = result
                }
            }
        } else {
            Log.d(TAG, "getUserData: ${response.errorBody()}")
        }
    }

    class Factory(private val application: Application, val userId: String): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserViewModel(UserRepository.getInstance(application)!!, userId) as T
        }
    }
}