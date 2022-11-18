package com.ssafy.smartstore.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class UserViewModel(user: User, app: Application) : AndroidViewModel(app) {
    private val _userInfo = MutableLiveData<User>()
    val userInfo: LiveData<User>
        get() = _userInfo

    init {
        _userInfo.value = user
    }






}