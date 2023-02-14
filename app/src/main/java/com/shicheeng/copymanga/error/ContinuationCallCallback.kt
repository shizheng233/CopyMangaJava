package com.shicheeng.copymanga.error

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CompletionHandler
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resumeWithException

class ContinuationCallCallback(
    private val call: Call,
    private val cancellation: CancellableContinuation<Response>,
) : Callback, CompletionHandler {

    override fun onFailure(call: Call, e: IOException) {
        if (!call.isCanceled() && cancellation.isActive) {
            cancellation.resumeWithException(e)
        }
    }

    override fun onResponse(call: Call, response: Response) {
        if (cancellation.isActive) {
            cancellation.resume(response)
        }
    }

    override fun invoke(cause: Throwable?) {
        runCatching {
            call.cancel()
        }.onFailure {
            cause?.addSuppressed(it)
        }
    }
}

fun <T> Continuation<T>.resume(value: T): Unit =
    resumeWith(Result.success(value))