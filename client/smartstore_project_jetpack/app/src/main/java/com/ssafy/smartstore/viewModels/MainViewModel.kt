package com.ssafy.smartstore.viewModels

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var flag = false
    var tableId : String = ""
        private set

    fun tableIdSet(id : String) {
        tableId = id
    }
}
