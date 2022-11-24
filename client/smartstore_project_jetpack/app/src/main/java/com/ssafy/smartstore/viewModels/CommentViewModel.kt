package com.ssafy.smartstore.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.repository.StoreRepository
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse
import kotlinx.coroutines.launch

class CommentViewModel(private val repository: StoreRepository, productId: Int) : ViewModel() {
    private var _commentList = MutableLiveData<List<MenuDetailWithCommentResponse>>()
    val commentList : LiveData<List<MenuDetailWithCommentResponse>>
        get() = _commentList

    init {
        viewModelScope.launch{
            _commentList.value = repository.getComments(productId)
        }
    }

    fun insertComment(comment: Comment) = viewModelScope.launch {
        val response = repository.insertComment(comment)
        if(response.isSuccessful) _commentList.value = repository.getComments(comment.productId)
    }

    fun updateComment(comment: Comment) = viewModelScope.launch {
        val response = repository.updateComment(comment)
        if(response.isSuccessful) _commentList.value = repository.getComments(comment.productId)
    }

    fun deleteComment(comment: Comment) = viewModelScope.launch {
        val response = repository.deleteComment(comment)
        if(response.isSuccessful) _commentList.value = repository.getComments(comment.productId)
    }

    class Factory(private val application: Application, val productId: Int): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CommentViewModel(StoreRepository.getInstance(application)!!, productId) as T
        }
    }
}
