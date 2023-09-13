package com.shicheeng.copymanga.util

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import android.util.TypedValue
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.annotation.MainThread
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.shicheeng.copymanga.data.ChipTextBean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

/**
 * 将[JsonArray]转化为[String]对象。
 */
fun JsonArray.authorNameReformation(): String =
    if (size() == 1) get(0).asJsonObject["name"].asString else get(0).asJsonObject["name"].asString + " 等"


@MainThread
inline fun <T> Flow<T>.collectRepeatLifecycle(
    lifecycleOwner: LifecycleOwner,
    crossinline collected: (T) -> Unit,
) {
    lifecycleOwner.lifecycle.coroutineScope.launch {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
            collectLatest { collected(it) }
        }
    }
}

@MainThread
inline fun <reified VM : ViewModel> Fragment.assistedViewModels(
    noinline factoryProducer: () -> VM,
): Lazy<VM> = viewModels {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return requireNotNull(modelClass.cast(factoryProducer.invoke()))
        }
    }
}

infix fun Context.openUrl(string: String){
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(string)))
}

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.assistedViewModels(
    noinline factoryProducer: () -> VM,
): Lazy<VM> = viewModels {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return requireNotNull(modelClass.cast(factoryProducer.invoke()))
        }
    }
}

fun PaddingValues.copy(
    layoutDirection: LayoutDirection,
    top: Dp = this.calculateTopPadding(),
    bottom: Dp = this.calculateBottomPadding(),
    start: Dp = this.calculateStartPadding(layoutDirection),
    end: Dp = this.calculateEndPadding(layoutDirection),
): PaddingValues {
    return PaddingValues(start = start, top = top, end = end, bottom = bottom)
}

@Composable
fun PaddingValues.copyComposable(
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    top: Dp = this.calculateTopPadding(),
    bottom: Dp = this.calculateBottomPadding(),
    start: Dp = this.calculateStartPadding(layoutDirection),
    end: Dp = this.calculateEndPadding(layoutDirection),
): PaddingValues {
    return PaddingValues(start = start, top = top, end = end, bottom = bottom)
}

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

/**
 * Refer from Kotatsu
 *
 * 新旧交替检测
 */
@Deprecated("不再使用LiveData")
fun <T> LiveData<T>.observeWithPrevious(owner: LifecycleOwner, observer: BufferedObserver<T>) {
    var previous: T? = null
    this.observe(owner) {
        observer.onChanged(it, previous)
        previous = it
    }
}

fun String.parserToJson(): JsonElement = JsonParser.parseString(this)

/**
 * 没有波纹动画的点击监听
 */
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.click(onClick: () -> Unit) = composed {
    combinedClickable(
        onClick = onClick,
        onLongClick = null,
        interactionSource = remember { MutableInteractionSource() },
        indication = null
    )
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

/**
 * 将大数字转化为可读性数字。没有i18n。
 */
fun Long.formNumberToRead(): String {

    return when {
        this >= 1000000000 -> {
            String.format("%.2f 亿", this / 1000000000.0)
        }

        this >= 10000000 -> {
            String.format("%.2f 千万", this / 1000000.0)
        }

        this >= 10000 -> {
            String.format("%.2f 万", this / 10000.0)
        }

        this >= 1000 -> {
            String.format("%.2f 千", this / 1000.0)
        }

        else -> this.toString()
    }

}

/**
 * Time convert
 */
fun String.timeStampConvert(): String {
    val sfd = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ROOT)
    val timeStamp = Instant.parse(this).toEpochMilli()
    return sfd.format(timeStamp)
}

/**
 * Copy from Kotatsu
 */
fun View.hasGlobalPoint(x: Int, y: Int): Boolean {
    if (visibility != View.VISIBLE) {
        return false
    }
    val rect = Rect()
    getGlobalVisibleRect(rect)
    return rect.contains(x, y)
}

/**
 * Format long to Time
 *
 * The format -> 2023/2/22 12:15
 */
@Deprecated("使用更加安全的方法")
fun Long.toTimeReadable(): String {
    val date = Date(this)
    val sfd = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ROOT)
    return sfd.format(date)
}

fun Long.convertToTimeGroup(): String {
    val sfd = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    return sfd.format(Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()))
}

fun Long.convertToOnlyTime(): String {
    val sfd = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    return sfd.format(Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()))
}


/**
 * Copy from Kotatsu
 */
fun <T> Collection<T>.asArrayList(): ArrayList<T> = if (this is ArrayList<*>) {
    this as ArrayList<T>
} else {
    ArrayList(this)
}

fun interface BufferedObserver<T> {
    fun onChanged(t: T, prev: T?)
}

val Context.animatorDurationScale: Float
    get() = Settings.Global.getFloat(
        this.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
    )
