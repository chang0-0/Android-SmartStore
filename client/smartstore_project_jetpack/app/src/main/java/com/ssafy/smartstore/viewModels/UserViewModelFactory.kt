package com.ssafy.smartstore.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ssafy.smartstore.api.UserApi
import com.ssafy.smartstore.repository.UserRepository

class UserViewModelFactory(
    private val userApi: UserApi,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel() as T
    }
}