package com.shicheeng.copymanga.util

import androidx.viewpager2.widget.ViewPager2

inline fun ViewPager2.onPageChangeCallback(crossinline position: (Int) -> Unit) {
    registerOnPageChangeCallback(
        object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                position(position)
            }
        }
    )
}