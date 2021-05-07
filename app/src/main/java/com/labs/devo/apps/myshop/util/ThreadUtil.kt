package com.labs.devo.apps.myshop.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

object ThreadUtil {

    suspend fun doOnMain(f: () -> Unit) {
        withContext(Dispatchers.Main) {
            f.invoke()
        }
    }

    suspend fun <T> doOnMainSync(coroutineContext: CoroutineContext = Dispatchers.Main, f: () -> T): T {
        return CoroutineScope(coroutineContext).async {
            f.invoke()
        }.await()
    }

}