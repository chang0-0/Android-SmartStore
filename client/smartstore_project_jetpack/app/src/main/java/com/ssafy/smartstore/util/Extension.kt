package com.ssafy.smartstore.util

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

fun Context.showToastMessage(message : String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.showSnackBarMessage(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}
