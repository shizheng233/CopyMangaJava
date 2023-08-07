package com.shicheeng.copymanga.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.textview.MaterialTextView

class TransitionTextview @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : MaterialTextView(context, attributeSet, defStyleAttr) {

    private val hideRunnable = Runnable {
        hide()
    }

    fun show(message: CharSequence) {
        removeCallbacks(hideRunnable)
        text = message
        setupTransition()
        isVisible = true
    }

    fun show(@StringRes resID: Int) {
        show(context.getString(resID))
    }

    fun tip(message: CharSequence, duration: Long) {
        show(message)
        postDelayed(hideRunnable, duration)
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(hideRunnable)
        super.onDetachedFromWindow()
    }

    fun hide() {
        removeCallbacks(hideRunnable)
        setupTransition()
        isVisible = false
    }

    private fun setupTransition() {
        val parentView = parent as? ViewGroup ?: return
        val transition = TransitionSet()
            .setOrdering(TransitionSet.ORDERING_TOGETHER)
            .addTarget(this)
            .addTransition(Slide(Gravity.TOP))
            .addTransition(Fade())
        TransitionManager.beginDelayedTransition(parentView, transition)
    }

}