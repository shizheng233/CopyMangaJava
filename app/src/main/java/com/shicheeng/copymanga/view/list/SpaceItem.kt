package com.shicheeng.copymanga.view.list

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

class SpaceItem(@Px private val spacingDp: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        outRect.set(spacingDp, spacingDp, spacingDp, spacingDp)
    }
}