package com.shicheeng.copymanga.util

import androidx.compose.runtime.Immutable
import com.shicheeng.copymanga.error.ContinuationCallCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Response

/**
 * Copy from [Kotatsu](https://github.com/KotatsuApp/Kotatsu).
 */
suspend fun Call.await(): Response = suspendCancellableCoroutine {
    val callback = ContinuationCallCallback(this, it)
    enqueue(callback)
    it.invokeOnCancellation(callback)
}

sealed class UIState<out T> {
    @Immutable
    data class Success<T>(val content: T) : UIState<T>()

    @Immutable
    data class Error<E : Exception>(val errorMessage: E) : UIState<Nothing>()

    @Immutable
    object Loading : UIState<Nothing>()
}

sealed class LoginState<out T> {
    @Immutable
    data class Success<T>(val content: T) : LoginState<T>()

    @Immutable
    data class Error<E : Exception>(val errorMessage: E) : LoginState<Nothing>()

    @Immutable
    object Loading : LoginState<Nothing>()

    @Immutable
    object NoStatus : LoginState<Nothing>()
}

sealed class SendUIState<T : Any> {
    @Immutable
    data class Success<T : Any>(val data: T) : SendUIState<T>()

    @Immutable
    data class Error<T : Any>(val errorMessage: Throwable) : SendUIState<T>()

    @Immutable
    object Loading : SendUIState<Nothing>()

    @Immutable
    object Idle : SendUIState<Nothing>()
}


