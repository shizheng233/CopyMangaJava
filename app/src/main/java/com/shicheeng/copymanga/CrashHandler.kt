package com.shicheeng.copymanga

import android.content.Context
import com.shicheeng.copymanga.error.ErrorActivity
import java.lang.Thread.UncaughtExceptionHandler

@Deprecated("先暂时弃用，这个方法还没学会")
class CrashHandler(
    private val context: Context,
) : UncaughtExceptionHandler {

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(
        thread: Thread,
        error: Throwable,
    ) {
        ErrorActivity.newIntentInstance(context = context, error.message).let {
            context.startActivity(it)
        }
    }


}