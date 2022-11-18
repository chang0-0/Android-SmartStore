package com.ssafy.smartstore.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ssafy.smartstore.dto.User

class UserViewModelFactory(
    private val user: User,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}