package com.ssafy.smartstore.dto

import Stamp


data class User(
    var id: String,
    var name: String,
    var pass: String,
    var stamps: Int,
    var stampList: ArrayList<Stamp> = ArrayList()
) : java.io.Serializable {
    constructor() : this("", "", "", 0)
    constructor(id: String, pass: String) : this(id, "", pass, 0)
}

// http://mobile-pjt.sample.ssafy.io/rest/user/info?id=iop90