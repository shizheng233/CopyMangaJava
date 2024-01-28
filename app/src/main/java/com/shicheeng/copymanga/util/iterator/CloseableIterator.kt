package com.shicheeng.copymanga.util.iterator

import okhttp3.internal.closeQuietly
import java.io.Closeable

class CloseableIterator<T>(
    private val upstream: Iterator<T>,
    private val closeable: Closeable,
) : Iterator<T>, Closeable {

    private var isClose = false

    override fun hasNext(): Boolean {
        val result = upstream.hasNext()
        if (!result) {
            close()
        }
        return result
    }

    override fun next(): T {
        try {
            return upstream.next()
        } catch (e: NoSuchElementException) {
            close()
            throw e
        }
    }

    override fun close() {
        if (!isClose){
            closeable.closeQuietly()
            isClose = true
        }
    }
}