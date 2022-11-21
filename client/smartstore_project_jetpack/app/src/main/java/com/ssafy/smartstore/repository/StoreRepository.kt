package com.ssafy.smartstore.repository

import android.app.Application
import com.google.gson.JsonPrimitive
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.dto.Order
import com.ssafy.smartstore.dto.Product
import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse
import com.ssafy.smartstore.response.OrderDetailResponse
import com.ssafy.smartstore.util.RetrofitUtil
import retrofit2.Response

class StoreRepository(application: Application) {
    suspend fun getComments(productId: Int): List<MenuDetailWithCommentResponse> {
        val response = RetrofitUtil.productService.getProductWithComments(productId)
        return response.body() as List<MenuDetailWithCommentResponse>
    }

    suspend fun insertComment(comment: Comment): Response<JsonPrimitive> {
        return RetrofitUtil.commentService.insert(comment)
    }

    suspend fun updateComment(comment: Comment): Response<JsonPrimitive> {
        return RetrofitUtil.commentService.update(comment)
    }

    suspend fun deleteComment(comment: Comment): Response<JsonPrimitive> {
        return RetrofitUtil.commentService.delete(comment.id)
    }

    suspend fun getOrderDetail(orderId: Int): Response<List<OrderDetailResponse>> {
        return RetrofitUtil.orderService.getOrderDetail(orderId)
    }

    suspend fun getLastMonthOrder(userId: String): Response<List<LatestOrderResponse>> {
        return RetrofitUtil.orderService.getLastMonthOrder(userId)
    }

    suspend fun getProductList(): Response<List<Product>>{
        return RetrofitUtil.productService.getProductList()
    }

    suspend fun makeOrder(order: Order): Response<Int> {
        return RetrofitUtil.orderService.makeOrder(order)
    }

    companion object {
        private var instance: StoreRepository? = null

        fun getInstance(application: Application): StoreRepository? {
            if (instance == null) instance = StoreRepository(application)
            return instance
        }
    }
}