package com.shicheeng.copymanga.fm.reader.noraml

import com.google.android.material.slider.LabelFormatter

class PageSliderFormatter : LabelFormatter {

    override fun getFormattedValue(value: Float): String {
        return (value + 1).toInt().toString()
    }

}