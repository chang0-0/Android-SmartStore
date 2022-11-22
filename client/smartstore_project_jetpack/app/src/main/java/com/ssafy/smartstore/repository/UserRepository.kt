package com.ssafy.smartstore.repository

import android.app.Application
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

class UserRepository() {
    private val userApi = ApplicationClass.retrofit.create(UserApi::class.java)

    suspend fun getUserData(userId: String): Response<UserInfoResponse> {
        //return userApi.getInfo(userId) 아래와 같은 코드
        return RetrofitUtil.userService.getInfo(userId)
    } // End of getUserData

    // ID 중복 체크
    suspend fun checkUserId(userId: String): Boolean {
        var result = false

        withContext(Dispatchers.IO) {
            var response = RetrofitUtil.userService.isUsedId(userId)

            if (response.isSuccessful) {
                result = response.body() as Boolean
            } else {
                Log.d(TAG, "getUserData: ${response.errorBody()}")
            }
        }

        return result
    } // End of checkUserId

    suspend fun joinUser(user: User): Boolean {
        var ans = false

        withContext(Dispatchers.IO) {
            Log.d(TAG, "UserImpl의 joinUser 메소드 안 ")
            var result = RetrofitUtil.userService.insert(user)
            Log.d(TAG, "joinUser의 withContext안 $result")

            if (result.isSuccessful) {
                ans = result.body() as Boolean
            } else {
                Log.d(TAG, "joinUser: ${result.errorBody()}")
            }
        }

        return ans
    } // End of joinUser

    suspend fun login(user: User): User {
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
    } // End of login

    companion object {
        private var instance: UserRepository? = null

        fun getInstance(application: Application): UserRepository? {
            if (instance == null) instance = UserRepository()
            return instance
        }
    } // companion object
}