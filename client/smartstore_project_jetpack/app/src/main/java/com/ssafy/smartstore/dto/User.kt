package com.ssafy.smartstore.dto

import Stamp

data class User (
    var id: String,
    var name: String,
    var pass: String,
    val stamps: Int,
    val stampList: ArrayList<Stamp> = ArrayList()
){
    constructor():this("", "","",0)
    constructor(id:String, pass:String):this(id, "",pass,0)
}