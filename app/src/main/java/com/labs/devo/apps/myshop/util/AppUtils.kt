package com.labs.devo.apps.myshop.util

import android.net.Uri
import com.labs.devo.apps.myshop.const.AppConstants.TAG
import java.io.File

fun checkIsImageBiggerInSize(uri: Uri, size: Int, exception: Exception) {
    try {
        uri.path?.let {
            val file = File(it)
            if (size < file.length()) throw exception
        } ?: throw exception
    } catch (ex: Exception) {
        if (exception.message != null)
            throw exception
        else throw java.lang.Exception(ex.message)
    }
}