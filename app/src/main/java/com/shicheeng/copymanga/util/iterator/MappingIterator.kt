package com.shicheeng.copymanga.util.iterator

class MappingIterator<T, R>(
    private val upstream: Iterator<T>,
    private val mapper: (T) -> R,
) : Iterator<R> {

    override fun hasNext(): Boolean {
        return upstream.hasNext()
    }

    override fun next(): R {
       return mapper(upstream.next())
    }
}