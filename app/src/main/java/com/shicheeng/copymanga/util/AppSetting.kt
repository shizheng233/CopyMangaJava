package com.shicheeng.copymanga.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class AppSetting {


    companion object {
        @Volatile
        private var INSTANCE: SharedPreferences? = null
        fun getInstance(context: Context): SharedPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = PreferenceManager.getDefaultSharedPreferences(context)
                INSTANCE = instance
                instance
            }
        }
    }


}