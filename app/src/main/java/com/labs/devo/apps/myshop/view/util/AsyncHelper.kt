package com.labs.devo.apps.myshop.view.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object AsyncHelper {
    suspend fun <T> runAsync(f: suspend () -> T): T {
        return withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            f.invoke()
        }
    }

    fun runAsyncInBackground(f: suspend () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            f.invoke()
        }
    }
}