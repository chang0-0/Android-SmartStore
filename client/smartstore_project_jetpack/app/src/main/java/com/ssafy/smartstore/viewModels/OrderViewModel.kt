package com.ssafy.smartstore.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "OrderViewModel_싸피"
class OrderViewModel : ViewModel() {
    private var _distance = 0
    val distance: Int
        get() = _distance


    fun updateDistance(dist:Int) {
        _distance = dist
        Log.d(TAG, "updateDistance: $distance")
    }
}
