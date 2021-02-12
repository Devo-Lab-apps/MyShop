package com.labs.devo.apps.myshop.view.util

import kotlinx.coroutines.*

object AsyncHelper {
    suspend fun <T> runAsync(f: suspend () -> T): T {
        return CoroutineScope(Dispatchers.IO).async {
            f.invoke()
        }.await()
    }

    fun runAsyncInBackground(f: suspend () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            f.invoke()
        }
    }
}