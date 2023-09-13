package com.shicheeng.copymanga.util

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope

val processLifecycleScope: LifecycleCoroutineScope
    inline get() = ProcessLifecycleOwner.get().lifecycleScope