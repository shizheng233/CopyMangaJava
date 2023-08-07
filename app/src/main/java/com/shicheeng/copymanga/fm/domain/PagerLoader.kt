package com.shicheeng.copymanga.fm.domain

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.collection.LongSparseArray
import androidx.collection.set
import com.shicheeng.copymanga.util.OkhttpHelper
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.invoke
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Headers
import okhttp3.Request
import okio.Closeable
import java.io.File
import java.io.InputStream
import java.util.LinkedList
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject


class PagerLoader @Inject constructor(
    private val cache: PagerCache,
    private val headers: Headers,
) : Closeable {

    val loaderScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val tasks = LongSparseArray<Deferred<File>>()
    private val prefetchQueue = LinkedList<String>()
    private val counter = AtomicInteger(0)
    private val convertLock = Mutex()


    private fun onIdle() {
        synchronized(prefetchQueue) {
            while (prefetchQueue.isNotEmpty()) {
                val url = prefetchQueue.pollFirst() ?: return
                if (cache[url] == null) {
                    synchronized(tasks) {
                        tasks[url.hashCode().toLong()] = loadPageAsync(url)
                    }
                    return
                }
            }
        }
    }

    fun loadImageFromUrlAsync(url: String, force: Boolean): Deferred<File> {
        if (!force) {
            cache[url]?.let {
                return getCompletedTaskAsync(it)
            }
        }
        var task = tasks[url.hashCode().toLong()]
        if (force) {
            task?.cancel()
        } else if (task?.isCancelled == false) {
            return task
        }
        task = loadPageAsync(url)
        synchronized(tasks) {
            tasks[url.hashCode().toLong()] = task
        }
        return task
    }

    private fun loadPageAsync(url: String): Deferred<File> {
        val deferred = loaderScope.async {
            try {
                loadPagePicBitmap(url)
            } finally {
                if (counter.decrementAndGet() == 0) {
                    onIdle()
                }
            }
        }
        return deferred
    }


    suspend fun convertInPlace(file: File) {
        convertLock.withLock {
            runInterruptible(Dispatchers.Default) {
                val image = BitmapFactory.decodeFile(file.absolutePath)
                try {
                    file.outputStream().use { out ->
                        image.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                } finally {
                    image.recycle()
                }
            }
        }
    }

    private suspend fun loadPagePicBitmap(url: String): File = Dispatchers.IO {
        val uri = Uri.parse(url)
        if (uri.scheme == "https") {
            val request = Request.Builder()
                .headers(headers).url(url).get()
                .build()
            OkhttpHelper.getInstance().newCall(request).execute().use { res ->
                val ins = checkNotNull(res.body).byteStream()
                cache.put(url, ins)
            }
        } else {
            val input: InputStream = File(url).inputStream()
            cache.put(url, input)
        }

    }

    private fun getCompletedTaskAsync(file: File): Deferred<File> {
        return CompletableDeferred(file)
    }

    override fun close() {
        loaderScope.cancel()
        synchronized(tasks) {
            tasks.clear()
        }
    }

}

