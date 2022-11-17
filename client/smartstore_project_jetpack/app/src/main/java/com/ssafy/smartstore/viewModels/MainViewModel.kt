package com.ssafy.smartstore.viewModels

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var tableId : String = ""
        private set

    fun tableIdSet(id : String) {
        tableId = id
    }

}
