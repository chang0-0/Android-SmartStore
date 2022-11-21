package com.ssafy.smartstore.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.repository.UserRepository
import com.ssafy.smartstore.util.RetrofitUtil
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

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
        val response = UserRepository().checkUserId(userId)

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
    fun joinUser(user: User) {

        val job1 = viewModelScope.async {
            _isCompleteJoin.value = UserRepository().joinUser(user)
        }

        viewModelScope.launch {
            Log.d(TAG, "LoginViewModel joinUser 실행됨")
            job1.await()
        }

    } // End of joinUser

    fun joinChangeState() {
        _isCompleteJoin.value = false
    }

    // 로그인
    fun login(user: User) {
        val job1 = viewModelScope.async {
            UserRepository().login(user)
        }

        viewModelScope.launch {
            _loginCheckUser.value = job1.await()
        }
    } // End of

} // End of LoginViewModel
