package com.ssafy.smartstore.api

import com.google.gson.JsonPrimitive
import com.ssafy.smartstore.dto.Comment
import retrofit2.Response
import retrofit2.http.*

interface CommentApi {
    // comment를 추가한다.
    @POST("rest/comment")
    suspend fun insert(@Body comment: Comment): Response<JsonPrimitive>

    // comment를 수정한다.
    @PUT("rest/comment")
    suspend fun update(@Body comment: Comment): Response<JsonPrimitive>

    // {id}에 해당하는 comment를 삭제한다.
    @DELETE("rest/comment/{id}")
    suspend fun delete(@Path("id") id: Int): Response<JsonPrimitive>
}