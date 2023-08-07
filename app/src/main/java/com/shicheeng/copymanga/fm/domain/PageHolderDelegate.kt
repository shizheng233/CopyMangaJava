package com.shicheeng.copymanga.fm.domain

import android.net.Uri
import androidx.core.net.toUri
import com.davemorrissey.labs.subscaleview.DefaultOnImageEventListener
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.io.File
import java.io.IOException

class PageHolderDelegate(
    private val loader: PagerLoader,
    private val callback: Callback,
) : DefaultOnImageEventListener {

    private var job: Job? = null
    private val scope = loader.loaderScope + Dispatchers.Main.immediate
    private var state = State.EMPTY
    private var file: File? = null

    fun onBind(url: String) {
        val prevJop = job
        job = scope.launch {
            prevJop?.cancelAndJoin()
            doLoad(url, false)
        }
    }

    fun retry(url: String) {
        val prevJob = job
        job = scope.launch {
            prevJob?.cancelAndJoin()
            doLoad(url, true)
        }
    }

    fun onRecycler() {
        state = State.EMPTY
        file = null
        job?.cancel()
    }

    override fun onReady() {
        super.onReady()
        state = State.SHOWING
    }

    override fun onImageLoaded() {
        super.onImageLoaded()
        state = State.SHOWN
        callback.onImageShown()
    }

    override fun onImageLoadError(e: Throwable) {
        val file = this.file
        if (state == State.LOADED && e is IOException && file != null && file.exists()) {
            tryConvert(file, e)
        } else {
            state = State.ERROR
            callback.onError(e = e)
        }
        callback.onError(e)
    }

    private suspend fun doLoad(url: String, force: Boolean) {
        state = State.LOADING
        callback.onLoadingStarted()
        try {
            val task = loader.loadImageFromUrlAsync(url, force)
            file = coroutineScope {
                task.await()
            }
            state = State.LOADED
            callback.onImageReady(checkNotNull(file).toUri())
        } catch (e: Exception) {
            state = State.ERROR
            callback.onError(e)
        }

    }

    private fun tryConvert(file: File, e: Exception) {
        val prevJob = job
        job = scope.launch {
            prevJob?.join()
            state = State.CONVERTING
            try {
                loader.convertInPlace(file)
                state = State.CONVERTED
                callback.onImageReady(file.toUri())
            } catch (ce: CancellationException) {
                throw ce
            } catch (e2: Throwable) {
                e.addSuppressed(e2)
                state = State.ERROR
                callback.onError(e = e)
            }
        }
    }

    private enum class State {
        EMPTY, LOADING, LOADED, CONVERTING, CONVERTED, SHOWING, SHOWN, ERROR
    }

    interface Callback {

        fun onLoadingStarted()

        fun onError(e: Throwable)

        fun onImageReady(uri: Uri)

        fun onImageShown()
    }

}


