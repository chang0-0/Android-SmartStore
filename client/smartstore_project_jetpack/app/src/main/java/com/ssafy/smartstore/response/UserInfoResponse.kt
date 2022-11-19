package com.ssafy.smartstore.response

import com.google.gson.annotations.SerializedName
import com.ssafy.smartstore.dto.Grade
import com.ssafy.smartstore.dto.Order
import com.ssafy.smartstore.dto.User
import java.util.*

// UserInfo response
data class UserInfoResponse(
    @SerializedName("grade") val grade: Grade,
    @SerializedName("user") var user: User,
    @SerializedName("order") val order: List<Order>,
)