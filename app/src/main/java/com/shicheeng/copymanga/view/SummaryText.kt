package com.shicheeng.copymanga.view

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.doOnNextLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.shicheeng.copymanga.util.animatorDurationScale
import kotlin.math.roundToLong

/**
 * 修改自Tachiyomi。
 *
 * 展开文字控件
 */
class SummaryText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    var expanded = false
        set(value) {
            if (field != value) {
                field = value
                updateExpandState()
            }
        }
    private var recalculateHeights = false
    private var descExpandedHeight = -1
    private var descShrunkHeight = -1
    private var animatorSet: AnimatorSet? = null


    private fun updateExpandState() {
        val initialSetup = maxHeight < 0

        val maxHeightTarget = if (expanded) descExpandedHeight else descShrunkHeight
        val maxHeightStart = if (initialSetup) maxHeightTarget else maxHeight
        val descMaxHeightAnimator = ValueAnimator().apply {
            setIntValues(maxHeightStart, maxHeightTarget)
            addUpdateListener {
                maxHeight = it.animatedValue as Int
            }
        }

        var pastHalf = false
        val toggleTarget = if (expanded) 1F else 0F
        val toggleStart = if (initialSetup) {
            toggleTarget
        } else {
            translationY / height
        }
        val toggleAnimator = ValueAnimator().apply {
            setFloatValues(toggleStart, toggleTarget)
            addUpdateListener {

                // Update non-animatable objects mid-animation makes it feel less abrupt
                if (it.animatedFraction >= 0.5F && !pastHalf) {
                    pastHalf = true
                    ellipsizeWhenNeeded()
                }
            }
        }

        animatorSet?.cancel()
        animatorSet = AnimatorSet().apply {
            interpolator = FastOutSlowInInterpolator()
            duration = (TOGGLE_ANIM_DURATION * context.animatorDurationScale).roundToLong()
            playTogether(toggleAnimator, descMaxHeightAnimator)
            start()
        }

    }


    private fun ellipsizeWhenNeeded() {
        return if (!expanded) {
            ellipsize = TextUtils.TruncateAt.END
        } else {
            ellipsize = null
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Wait until parent view has determined the exact width
        // because this affect the description line count
        val measureWidthFreely = MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY
        if (!recalculateHeights || measureWidthFreely) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        recalculateHeights = false

        // Measure with expanded lines
        maxLines = Int.MAX_VALUE
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        descExpandedHeight = measuredHeight

        // Measure with shrunk lines
        maxLines = SHRUNK_DESC_MAX_LINES
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        descShrunkHeight = measuredHeight
    }


    init {
        recalculateHeights = true
        doOnNextLayout {
            updateExpandState()
        }
        if (!isInLayout) {
            requestLayout()
        }
        minLines = DESC_MIN_LINES
        setOnClickListener { expanded = !expanded }
    }

}

private const val TOGGLE_ANIM_DURATION = 300L

private const val DESC_MIN_LINES = 2
private const val SHRUNK_DESC_MAX_LINES = 3