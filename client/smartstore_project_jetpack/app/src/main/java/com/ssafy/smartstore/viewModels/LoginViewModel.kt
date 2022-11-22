package com.ssafy.smartstore.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

private const val TAG = "LoginViewModel_싸피"

class LoginViewModel : ViewModel() {

    private val _isUsedId = MutableLiveData<Boolean>()
    val isUsedId: LiveData<Boolean>
        get() = _isUsedId

    private val _isCompleteJoin = MutableLiveData<Boolean?>()
    val isCompleteJoin: LiveData<Boolean?>
        get() = _isCompleteJoin

    private val _loginCheckUser = MutableLiveData<User>()
    val loginCheckUser: LiveData<User>
        get() = _loginCheckUser

    // ID 중복 체크
    fun checkUsedId(userId: String) {
        val job1 = viewModelScope.async {
            UserRepository().checkUserId(userId)
        }

        viewModelScope.launch {
            _isUsedId.value = job1.await() as Boolean
        }

    } // End of checkUsedId

    // 유저 회원가입
    fun joinUser(user: User) {
        val job1 = viewModelScope.async {
            UserRepository().joinUser(user)
        }

        viewModelScope.launch {
            _isCompleteJoin.value = job1.await()
        }

    } // End of joinUser

    // 로그인
    fun login(user: User) {
        val job1 = viewModelScope.async {
            UserRepository().login(user)
        }

        viewModelScope.launch {
            _loginCheckUser.value = job1.await()
        }
    } // End of login

    // 로그인 성공 여부 flag값 변경
    fun stateChange() {
        _isCompleteJoin.value = null
    }

} // End of LoginViewModel
