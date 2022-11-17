package com.ssafy.smartstore.api

import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.dto.User
import retrofit2.Call
import retrofit2.http.*

interface CommentApi {
    // comment를 추가한다.
    @POST("rest/comment")
    fun insert(@Body comment: Comment): Call<Boolean>

    // comment를 수정한다.
    @PUT("rest/comment")
    fun update(@Body comment: Comment): Call<Boolean>

    // {id}에 해당하는 comment를 삭제한다.
    @DELETE("rest/comment/{id}")
    fun delete(@Path("id") id: Int): Call<Boolean>
}