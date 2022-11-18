package com.ssafy.smartstore.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.ssafy.smartstore.api.ProductApi
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse
import com.ssafy.smartstore.util.RetrofitUtil

private const val TAG = "StoreRepository_싸피"
class StoreRepository(application: Application) {
    suspend fun getComments(productId: Int): List<MenuDetailWithCommentResponse> {
        Log.d(TAG, "getComments: productId: ${productId}")
        val response = RetrofitUtil.productService.getProductWithComments(productId)
        return response.body() as List<MenuDetailWithCommentResponse>
    }


    companion object {
        private var instance: StoreRepository? = null

        fun getInstance(application: Application): StoreRepository? {
            if (instance == null) instance = StoreRepository(application)
            return instance
        }
    }
}