package com.ssafy.smartstore.viewModels

import android.util.Log
import androidx.lifecycle.*
import com.ssafy.smartstore.api.UserApi
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.repository.UserRepository
import com.ssafy.smartstore.repository.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "UserViewModel_싸피"

// fragment -> viewModel -> repository -> api -> server(okhttp) -> api -> repository -> viewModel -> fragment
class UserViewModel() :
    ViewModel() {

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User>
        get() = _userData

    // LiveData를 관리하는 함수를 만드는거임
    // viewModel에서 repository를 호출한다.
    // viewModel 에서 호출하면, repository에 오고,

    // 사용자 데이터 가져오기
    suspend fun getUserData(userId: String) = viewModelScope.launch {
        val response = UserRepositoryImpl().getUserData(userId)

        Log.d(TAG, "ViewModel getUserData:${response} ")
        Log.d(TAG, "ViewModel getUserData:${response.body()} ")

        if (response.isSuccessful) {
            val result = response.body()

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
}