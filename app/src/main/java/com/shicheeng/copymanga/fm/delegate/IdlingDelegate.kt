package com.shicheeng.copymanga.fm.delegate

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.TimeUnit

/**
 * 大部分代码来自Kotatsu。
 */
class IdlingDelegate(private val idleCallback: IdleCallback) : DefaultLifecycleObserver {


    private val handler = Handler(Looper.getMainLooper())
    private val idleRunnable = Runnable {
        idleCallback.onIdle()
    }

    fun bindToLifecycle(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
    }

    fun onUserInteraction() {
        handler.removeCallbacks(idleRunnable)
        handler.postDelayed(idleRunnable, TimeUnit.SECONDS.toMillis(10))
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        owner.lifecycle.removeObserver(this)
        handler.removeCallbacks(idleRunnable)
    }

    fun interface IdleCallback {
        fun onIdle()
    }

}