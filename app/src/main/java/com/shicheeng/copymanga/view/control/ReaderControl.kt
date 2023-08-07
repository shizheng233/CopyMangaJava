package com.shicheeng.copymanga.view.control

import android.view.SoundEffectConstants
import android.view.View
import com.shicheeng.copymanga.fm.reader.ReaderMode
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import com.shicheeng.copymanga.util.GestureHelper as GridTouchHelper

class ReaderControl(
    private val listener: ControlDelegateListener,
    private val settingPref: SettingPref,
) {
    private val isQuickTouchEnable get() = settingPref.hyperTouch.value

    fun onGridTouch(area: Int, view: View) {
        when (area) {
            GridTouchHelper.AREA_CENTER -> {
                listener.hide()
                view.playSoundEffect(SoundEffectConstants.CLICK)
            }

            GridTouchHelper.AREA_TOP -> if (isQuickTouchEnable) {
                listener.scrollPage(-1)
                view.playSoundEffect(SoundEffectConstants.NAVIGATION_UP)
            }

            GridTouchHelper.AREA_LEFT -> {
                listener.scrollPage(if (isReaderTapsReversed()) -1 else 1)
                view.playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT)
            }

            GridTouchHelper.AREA_BOTTOM -> if (isQuickTouchEnable) {
                listener.scrollPage(1)
                view.playSoundEffect(SoundEffectConstants.NAVIGATION_DOWN)
            }

            GridTouchHelper.AREA_RIGHT -> {
                listener.scrollPage(if (isReaderTapsReversed()) 1 else -1)
                view.playSoundEffect(SoundEffectConstants.NAVIGATION_RIGHT)
            }
        }
    }

    private fun isReaderTapsReversed(): Boolean {
        return listener.readerMode == ReaderMode.STANDARD || listener.readerMode == ReaderMode.WEBTOON
    }

    interface ControlDelegateListener {
        val readerMode: ReaderMode?
        fun scrollPage(delta: Int)
        fun hide()
    }

}