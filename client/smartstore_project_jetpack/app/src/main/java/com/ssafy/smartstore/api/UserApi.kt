package com.ssafy.smartstore.api

import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.response.UserInfoResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {

    // 사용자 정보를 추가한다(회원가입)
    @POST("rest/user")
    suspend fun insert(@Body body: User): Response<Boolean>

    // 사용자의 정보와 함께 사용자의 주문 내역, 사용자 등급 정보를 반환한다.
    @GET("rest/user/info") // rest/user/info?id=iop90 형식
    suspend fun getInfo(@Query("id") id: String): Response<UserInfoResponse>

    // request parameter로 전달된 id가 이미 사용중인지 반환한다.
    @GET("rest/user/isUsed")
    suspend fun isUsedId(@Query("id") id: String): Response<Boolean>

    // 로그인 처리 후 성공적으로 로그인 되었다면 loginId라는 쿠키를 내려준다.
    @POST("rest/user/login")
    suspend fun login(@Body body: User): User
}


/*
Query와 Path가 헷갈릴 때 좋은 예시
출처 : https://stackoverflow.com/questions/37698501/retrofit-2-path-vs-query

Consider this is the url:
www.app.net/api/searchtypes/862189/filters?Type=6&SearchText=School

Now this is the call:

@GET("/api/searchtypes/{Id}/filters")
Call<FilterResponse> getFilterList(
          @Path("Id") long customerId,
          @Query("Type") String responseType,
          @Query("SearchText") String searchText
);

So we have:

www.app.net/api/searchtypes/{Path}/filters?Type={Query}&SearchText={Query}

Things that come after the ? are usually queries.

요악 : 보통 ?는 query로 많이 쓰인다...
 */
