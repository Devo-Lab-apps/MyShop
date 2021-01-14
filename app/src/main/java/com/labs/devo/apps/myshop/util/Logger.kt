package com.labs.devo.apps.myshop.util

import android.util.Log
import com.labs.devo.apps.myshop.util.Constants.DEBUG
import com.labs.devo.apps.myshop.util.Constants.TAG


var isUnitTest = false

fun printLogD(className: String?, vararg message: Any) {
    if (DEBUG && !isUnitTest) {
        for (msg in message)
            Log.d(TAG, "$className: $msg")
    } else if (DEBUG && isUnitTest) {
        println("$className: $message")
    }
}

/*
    Priorities: Log.DEBUG, Log. etc....
 */
fun cLog(msg: String?) {
    msg?.let {
        if (!DEBUG) {
//            FirebaseCrashlytics.getInstance().log(it)
        }
    }

}
