package com.shicheeng.copymanga.util

import com.shicheeng.copymanga.error.ContinuationCallCallback
import com.shicheeng.copymanga.json.MangaInfoJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.InputStream

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

suspend inline fun withGet(url: String, crossinline block: (InputStream) -> Unit) =
    withContext(Dispatchers.Default) {
        val request = Request.Builder().url(url).headers(MangaInfoJson.headers).get().build()
        val call = OkhttpHelper.getInstance().newCall(request)
        val response = call.await()
        val inputStream: InputStream = checkNotNull(response.body).byteStream()
        block(inputStream)
    }

/**
 * Copy from [Kotatsu](https://github.com/KotatsuApp/Kotatsu)
 */
suspend fun Call.await(): Response = suspendCancellableCoroutine {
    val callback = ContinuationCallCallback(this, it)
    enqueue(callback)
    it.invokeOnCancellation(callback)
}
