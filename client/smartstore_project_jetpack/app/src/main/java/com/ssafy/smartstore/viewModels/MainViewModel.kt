package com.ssafy.smartstore.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.smartstore.api.ProductApi
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstore.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "MainViewModel_싸피"
class MainViewModel : ViewModel() {
    var tableId : String = ""
        private set

    fun tableIdSet(id : String) {
        tableId = id
    }

    private var productInfo = MutableLiveData<List<MenuDetailWithCommentResponse>>()
    val _productInfo : LiveData<List<MenuDetailWithCommentResponse>>
        get() = productInfo

//    fun getProductInfo(productId: Int, callback: RetrofitCallback<List<MenuDetailWithCommentResponse>>) {
//        val menuInfoRequest: Call<List<MenuDetailWithCommentResponse>> = RetrofitUtil.productService.getProductWithComments(productId)
//
//        menuInfoRequest.enqueue(object : Callback<List<MenuDetailWithCommentResponse>> {
//            override fun onResponse(call: Call<List<MenuDetailWithCommentResponse>>, response: Response<List<MenuDetailWithCommentResponse>>) {
//                val res = response.body()
//                Log.d(TAG, "onResponse: response: ${res}")
//                if(response.code() == 200){
//                    if (res != null) {
//                        callback.onSuccess(response.code(), res)
//                        productInfo.value = res!!
//                    }
//                } else {
//                    callback.onFailure(response.code())
//                }
//            }
//
//            override fun onFailure(call: Call<List<MenuDetailWithCommentResponse>>, t: Throwable) {
//                callback.onError(t)
//            }
//        })
//    }

}
