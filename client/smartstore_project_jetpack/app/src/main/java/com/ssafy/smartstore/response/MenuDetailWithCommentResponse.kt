package com.ssafy.smartstore.response

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import androidx.databinding.library.baseAdapters.BR

data class MenuDetailWithCommentResponse (
    @SerializedName("img") val productImg: String,
    @SerializedName("avg") val productRatingAvg: Double,
    @SerializedName("user_id") val userId: String?,
    @SerializedName("sells") val productTotalSellCnt: Int,
    @SerializedName("price") val productPrice: Int,
    @SerializedName("name") val productName: String,
    @SerializedName("rating") val productRating: Double,
    @SerializedName("commentId") val commentId: Int = -1,
    @SerializedName("comment") val commentContent: String?,
    @SerializedName("userName") val commentUserName: String?,
    @SerializedName("commentCnt") val productCommentTotalCnt: Int,
    @SerializedName("type") val type: String
) : BaseObservable() {
    @get:Bindable
    var isClicked: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.clicked)  // BR : Binding된 Resource ID가 관리되는 Class
        }

}