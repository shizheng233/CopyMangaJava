package com.shicheeng.copymanga.fm.reader.webtoon

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewParent
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

/**
 * 如果一个图片没有加载出来，这个控件就将是最小的。那么，在这种情况下，滑倒某某页就不能使用。所以必须重写
 * [WebtoonImageView.getSuggestedMinimumHeight]方法。以达到效果。
 *
 * @author Kotatsu
 */
class WebtoonImageView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
) : SubsamplingScaleImageView(context, attr) {

    override fun getSuggestedMinimumHeight(): Int {
        var desiredHeight = super.getSuggestedMinimumHeight()
        if (sHeight == 0) {
            val parentHeight = parentHeight()
            if (desiredHeight < parentHeight) {
                desiredHeight = parentHeight
            }
        }
        return desiredHeight
    }

    private fun parentHeight(): Int {
        return parents.firstNotNullOfOrNull { it as? RecyclerView }?.height ?: 0
    }

    private val View.parents: Sequence<ViewParent>
        get() = sequence {
            var p: ViewParent? = parent
            while (p != null) {
                yield(p)
                p = p.parent
            }
        }

}