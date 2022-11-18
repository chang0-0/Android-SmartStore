package com.ssafy.smartstore.repository

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.smartstore.dto.User

class UserRepository private constructor(context : Context) {
    // 사용자 데이터 repository


    companion object {
        private var INSTANCE : UserRepository? = null

        fun initialize(context : Context) {
            if(INSTANCE == null) {
                INSTANCE = UserRepository(context)
            }
        }

        fun get() : UserRepository {
            return INSTANCE ?: throw IllegalArgumentException("User")
        }

    }

}