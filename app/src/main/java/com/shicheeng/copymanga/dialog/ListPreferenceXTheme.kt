package com.shicheeng.copymanga.dialog

import android.content.Context
import android.util.AttributeSet
import androidx.preference.ListPreference

class ListPreferenceXTheme @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = androidx.preference.R.attr.dialogPreferenceStyle,
    defStyleRes: Int = 0,
) :ListPreference(context,attr,defStyleAttr, defStyleRes){

}