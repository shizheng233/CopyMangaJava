package com.shicheeng.copymanga.data

import androidx.annotation.DrawableRes

data class ChipTextBean(
    var text: String,
    var pathWord: String,
    @DrawableRes val ids: Int,
)