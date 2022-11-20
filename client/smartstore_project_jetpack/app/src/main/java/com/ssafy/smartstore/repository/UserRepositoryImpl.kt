package com.ssafy.smartstore.repository

import com.ssafy.smartstore.api.UserApi
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.response.UserInfoResponse
import com.ssafy.smartstore.util.RetrofitUtil
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

    override fun login(user: User): Call<User> {
        return RetrofitUtil.userService.login(user)
    }
}