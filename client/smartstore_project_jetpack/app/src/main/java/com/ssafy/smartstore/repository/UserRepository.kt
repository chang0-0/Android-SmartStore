package com.ssafy.smartstore.repository

import android.content.Context
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.util.RetrofitUtil

class UserRepository constructor(
    context: Context
) {
    // 사용자 데이터 repository
    // DB조회 하는 부분을 LiveData로 바꾸면 동일하게 자동 갱신할 수 있다.


    fun getUserData(userId: String): User {
        var user = User()

        // api에 접근해서 데이터를 가져옴.
        // okHttp, Retrofit
        val response = RetrofitUtil.userService.getInfo(userId)
        user = response.body() as User
        return user
    }


    companion object {
        private var INSTANCE: UserRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = UserRepository(context)
            }
        }

        fun get(): UserRepository {
            return INSTANCE ?: throw IllegalArgumentException("User")
        }

    }

}