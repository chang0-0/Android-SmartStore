package com.ssafy.smartstore.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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


    // 사용자 ID 중복 체크
    suspend fun checkUsedId(userId: String) = viewModelScope.launch {
        val response = UserRepositoryImpl().checkUserId(userId)

        response.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if(response.isSuccessful) {
                    val res = response.body() as Boolean
                    _isUsedId.value = res
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d(TAG, "onFailure: $t")
                Log.d(TAG, "onFailure: ${call.isExecuted}")
            }
        })

//        if (response.isSuccessful) {
//            withContext(Dispatchers.IO) {
//                if (response.body() != null) {
//                    _isUsedId.value = response.body()
//                }
//            }
//        } else {
//            Log.d(TAG, "checkUsedId: ${response.errorBody()}")
//        }
    }

}