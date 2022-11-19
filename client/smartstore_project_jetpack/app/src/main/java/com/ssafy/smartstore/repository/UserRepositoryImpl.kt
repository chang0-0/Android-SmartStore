package com.ssafy.smartstore.repository

import android.util.Log
import com.google.gson.JsonPrimitive
import com.ssafy.smartstore.api.UserApi
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "UserRepositoryImpl_싸피"
class UserRepositoryImpl : UserRepository {
    private val userApi = ApplicationClass.retrofit.create(UserApi::class.java)

    override suspend fun getUserData(userId: String): Response<User> {
        //return userApi.getInfo(userId)
        return RetrofitUtil.userService.getInfo(userId)
    }

    override suspend fun checkUserId(userId: String): Call<Boolean> {
        Log.d(TAG, "checkUserId:  before")
        Log.d(TAG, "checkUserId: ${userId}")
        return RetrofitUtil.userService.isUsedId(userId)
    }
}