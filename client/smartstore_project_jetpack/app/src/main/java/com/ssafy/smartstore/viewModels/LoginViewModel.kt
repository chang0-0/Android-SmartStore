package com.ssafy.smartstore.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.repository.UserRepositoryImpl
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "LoginViewModel_싸피"

class LoginViewModel : ViewModel() {

    private val _isUsedId = MutableLiveData<Boolean>()
    val isUsedId: LiveData<Boolean>
        get() = _isUsedId

    private val _isCompleteJoin = MutableLiveData<Boolean>()
    val isCompleteJoin: LiveData<Boolean>
        get() = _isCompleteJoin


    private val _loginCheckUser = MutableLiveData<User>()
    val loginCheckUser: LiveData<User>
        get() = _loginCheckUser


    // 사용자 ID 중복 체크
    suspend fun checkUsedId(userId: String) = viewModelScope.launch {
        val response = UserRepositoryImpl().checkUserId(userId)

        response.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val res = response.body() as Boolean
                    _isUsedId.value = res
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d(TAG, "onFailure: $t")
            }
        })

    } // End of suspend checkUsedId

    // 유저 회원가입
    suspend fun joinUser(user: User) = viewModelScope.launch {
        val response = UserRepositoryImpl().joinUser(user)

        response.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val res = response.body() as Boolean
                    _isCompleteJoin.value = res
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d(TAG, "onFailure: $t")
            }
        })
    } // End of suspend joinUser

    // 로그인
    suspend fun login(user: User) = viewModelScope.launch {
        val response = UserRepositoryImpl().login(user)

        response.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "onResponse: ${response.body()}")

                    _loginCheckUser.value = response.body() as User
                } else {
                    Log.d(TAG, "onResponse: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d(TAG, "onFailure: $t")
            }
        })
    } // End of login

} // End of LoginViewModel