package com.ssafy.smartstore.repository

import com.ssafy.smartstore.dto.User
import retrofit2.Response

private const val TAG = "UserRepository_싸피"

interface UserRepository {
    // 사용자 데이터 repository
    // DB조회 하는 부분을 LiveData로 바꾸면 동일하게 자동 갱신할 수 있다.

    // user데이터 가져오기
    suspend fun getUserData(userId: String): Response<User>

//    companion object {
//        private var INSTANCE: UserRepository? = null
//
//        fun initialize(context: Context) {
//            if (INSTANCE == null) {
//                INSTANCE = UserRepository(userApi)
//            }
//        }
//
//        fun get(): UserRepository {
//            return INSTANCE ?: throw IllegalArgumentException("User")
//        }
//    }
}