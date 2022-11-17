package com.ssafy.smartstore.adapter

import android.view.View

interface AdapterItemClickListener {
    fun onClick(view : View, position : Int, p1 : Any?)
}