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

        /*
        if (response.isSuccessful) {
            withContext(Dispatchers.IO) {
                if (response.body() != null) {
                    _isUsedId.value = response.body()
                }
            }
        } else {
            Log.d(TAG, "checkUsedId: ${response.errorBody()}")
        }
         */

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
} // End of LoginViewModel