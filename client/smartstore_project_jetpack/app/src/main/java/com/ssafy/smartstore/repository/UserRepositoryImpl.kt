package com.ssafy.smartstore.repository

import android.util.Log
import com.ssafy.smartstore.api.UserApi
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.response.UserInfoResponse
import com.ssafy.smartstore.util.RetrofitUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response

private const val TAG = "UserRepositoryImpl_싸피"

class UserRepositoryImpl : UserRepository {
    private val userApi = ApplicationClass.retrofit.create(UserApi::class.java)

    override suspend fun getUserData(userId: String): Response<UserInfoResponse> {
        //return userApi.getInfo(userId) 아래와 같은 코드
        return RetrofitUtil.userService.getInfo(userId)
    }

    override suspend fun checkUserId(userId: String): Call<Boolean> {
        return RetrofitUtil.userService.isUsedId(userId)
    }

    override suspend fun joinUser(user: User): Call<Boolean> {
        return RetrofitUtil.userService.insert(user)
    }

    override suspend fun login(user: User): User {
        var result = User()

        withContext(Dispatchers.IO) {
            try {
                result = RetrofitUtil.userService.login(user)
                Log.d(TAG, "login: ${result}")
            } catch (e: Exception) {
                Log.d(TAG, "login: ")
            }
        }

        return result
    }
}