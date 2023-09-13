package com.shicheeng.copymanga.fm.reader.webtoon

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import com.shicheeng.copymanga.R

class WebtoonFrameLayout @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	@AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

	private val target by lazy(LazyThreadSafetyMode.NONE) {
		findViewById<WebtoonImageView>(R.id.biv_pager_webtoon)
	}

	fun dispatchVerticalScroll(dy: Int): Int {
		if (dy == 0) {
			return 0
		}
		val oldScroll = target.getScroll()
		target.scrollBy(dy)
		return target.getScroll() - oldScroll
	}
}