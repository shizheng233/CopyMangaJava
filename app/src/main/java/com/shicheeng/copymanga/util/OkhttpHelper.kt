package com.shicheeng.copymanga.util

import com.shicheeng.copymanga.error.ContinuationCallCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response

class OkhttpHelper {

    companion object {
        @Volatile
        private var INSTANCE: OkHttpClient? = null
        fun getInstance(): OkHttpClient {
            return INSTANCE ?: synchronized(this) {
                val instance = OkHttpClient()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Copy from [Kotatsu](https://github.com/KotatsuApp/Kotatsu)
 */
suspend fun Call.await(): Response = suspendCancellableCoroutine {
    val callback = ContinuationCallCallback(this, it)
    enqueue(callback)
    it.invokeOnCancellation(callback)
}
