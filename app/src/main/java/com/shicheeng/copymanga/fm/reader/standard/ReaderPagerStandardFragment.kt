package com.shicheeng.copymanga.fm.reader.standard

import android.view.View
import com.shicheeng.copymanga.fm.reader.noraml.ReaderPageFragment

class ReaderPagerStandardFragment : ReaderPageFragment() {

    override fun changeDirection(): Int {
        return View.LAYOUT_DIRECTION_LTR
    }
}