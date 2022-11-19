package com.ssafy.smartstore.repository

import com.ssafy.smartstore.api.UserApi
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.util.RetrofitUtil
import retrofit2.Response

class UserRepositoryImpl : UserRepository {
    private val userApi = ApplicationClass.retrofit.create(UserApi::class.java)

    override suspend fun getUserData(userId: String): Response<User> {
       //return userApi.getInfo(userId)
        return RetrofitUtil.userService.getInfo(userId)
    }
}