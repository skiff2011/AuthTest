package com.github.skiff2011.authtest

import android.content.Context
import android.widget.Toast

fun Context.toast(
    message: String?,
    duration: Int = Toast.LENGTH_SHORT
) {
    if (message != null) {
        Toast.makeText(this, message, duration)
            .show()
    }
}

fun Context.toast(
    resId: Int?,
    duration: Int = Toast.LENGTH_SHORT
) {
    if (resId != null) {
        toast(getString(resId), duration)
    }
}