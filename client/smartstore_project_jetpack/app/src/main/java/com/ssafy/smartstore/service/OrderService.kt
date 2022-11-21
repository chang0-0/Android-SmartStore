package com.ssafy.smartstore.service

import android.util.Log
import com.ssafy.smartstore.dto.Order
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstore.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "OrderService_싸피"

class OrderService {
    fun makeOrder(order: Order, callback: RetrofitCallback<Int>) {
        RetrofitUtil.orderService.makeOrder(order).enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                val res = response.body()
                Log.d(TAG, "makeOrder: ${order}")
                Log.d(TAG, "makeOrder: res: ${response.code()}")

                if (response.code() == 200) {
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                callback.onError(t)
            }
        })
    }
}