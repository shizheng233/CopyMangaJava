package com.shicheeng.copymanga.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MyRecyclerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
) : RecyclerView(context, attributeSet) {
    private val layout = LinearLayoutManager(context, HORIZONTAL, false)

    init {
        layoutManager = layout
    }
}