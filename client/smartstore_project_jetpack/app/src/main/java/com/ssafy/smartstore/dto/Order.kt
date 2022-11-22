package com.ssafy.smartstore.dto

import Stamp
import java.util.*

data class Order(
    var id: Int,
    var userId: String,
    var orderTable: String,
    var orderTime: String,
    var completed: String,
    val details: ArrayList<OrderDetail> = ArrayList()
) {

    var totalQuantity:Int = 0
    var totalPrice:Int = 0
    var topProductName:String = ""
    var topImg:String = ""

    var stamp = Stamp()
    constructor():this(0,"","","","")
}
