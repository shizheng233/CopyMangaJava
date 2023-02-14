@file:Suppress("DEPRECATION")

package com.shicheeng.copymanga.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.shicheeng.copymanga.adapter.MangaListAdapter
import com.shicheeng.copymanga.adapter.MangaLoadStateAdapter
import com.shicheeng.copymanga.data.ChipTextBean

fun JsonArray.authorNameReformation(): String =
    if (size() == 1) get(0).asJsonObject["name"].asString else get(0).asJsonObject["name"].asString + " 等"

fun GridLayoutManager.applySpanCountWithFooter(concatAdapter: ConcatAdapter) {
    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (concatAdapter.getItemViewType(position)) {
                MangaLoadStateAdapter.VIEW_TYPE_FOOTER -> 1
                MangaListAdapter.VIEW_TYPE_MAIN -> 2
                else -> 2
            }
        }
    }
}

fun String.checkJsonIsEmpty(): Boolean =
    JsonParser.parseString(this).asJsonObject["results"].asJsonObject["list"].asJsonArray.isEmpty

/**
 *
 * Copy from [tachiyomi](https://github.com/tachiyomiorg/tachiyomi/blob/820ed6a46880af1e9390706dc9915f3c7d385c60/app/src/main/java/eu/kanade/tachiyomi/util/system/ContextExtensions.kt)
 *
 */
@ColorInt
fun Context.getThemeColor(attr: Int): Int {
    val tv = TypedValue()
    return if (this.theme.resolveAttribute(attr, tv, true)) {
        if (tv.resourceId != 0) {
            getColor(tv.resourceId)
        } else {
            tv.data
        }
    } else {
        0
    }
}

/**
 * Returns a deep copy of the provided [Drawable]
 *
 * Copy from tachiyomi
 */
inline fun <reified T : Drawable> T.copy(context: Context): T? {
    return (constantState?.newDrawable()?.mutate() as? T).apply {
        if (this is MaterialShapeDrawable) {
            initializeElevationOverlay(context)
        }
    }
}

fun <T> LiveData<T>.observeWithPrevious(owner: LifecycleOwner, observer: BufferedObserver<T>) {
    var previous: T? = null
    this.observe(owner) {
        observer.onChanged(it, previous)
        previous = it
    }
}

fun RecyclerView.findCurrentPagePosition(): Int {
    val x = width / 2f
    val y = height / 2f
    val view = findChildViewUnder(x, y) ?: return RecyclerView.NO_POSITION
    return getChildAdapterPosition(view)
}

fun RecyclerView.setFirstVisibleItemPositionSmooth(position: Int, smooth: Boolean) {
    if (position != RecyclerView.NO_POSITION) {
        if (smooth) {
            smoothScrollToPosition(position)
        } else {
            scrollToPosition(position)
        }
    }
}

/**
 * Return recycler first item position
 */
var RecyclerView.firstVisibleItemPosition: Int
    get() = (layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
        ?: RecyclerView.NO_POSITION
    set(value) {
        if (value != RecyclerView.NO_POSITION) {
            (layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(value, 0)
        }
    }

fun ChipGroup.addChips(list: List<ChipTextBean>, onChipClick: ((Chip, String) -> Unit)? = null) {
    removeAllViews()
    list.forEach { chipTextBean ->
        val chip = Chip(context)
        chip.text = chipTextBean.text
        chip.setEnsureMinTouchTargetSize(false)
        chip.chipIcon = AppCompatResources.getDrawable(context, chipTextBean.ids)
        if (onChipClick != null) {
            chip.setOnClickListener { onChipClick(chip, chipTextBean.pathWord) }
        }
        addView(chip)
    }
}

fun String.parserAsJson(): JsonElement = JsonParser.parseString(this)

fun SelectionTracker<Long>.addLongChangeObserver(onChange: () -> Unit) {
    this.addObserver(object : SelectionTracker.SelectionObserver<Long>() {

        override fun onSelectionChanged() {
            super.onSelectionChanged()
            onChange.invoke()
        }

    })
}

fun Long.formNumberToRead(): String {

    return when {
        this >= 1000000000 -> {
            String.format("%.2fB", this / 1000000000.0)
        }
        this >= 1000000 -> {
            String.format("%.2fM", this / 1000000.0)
        }
        this >= 100000 -> {
            String.format("%.2fL", this / 100000.0)
        }
        this >= 10000 -> {
            String.format("%.2fW", this / 10000.0)
        }
        this >= 1000 -> {
            String.format("%.2fK", this / 1000.0)
        }
        else -> this.toString()
    }

}

fun interface BufferedObserver<T> {
    fun onChanged(t: T, prev: T?)
}