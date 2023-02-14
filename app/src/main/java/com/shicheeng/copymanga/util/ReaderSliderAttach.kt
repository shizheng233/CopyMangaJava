package com.shicheeng.copymanga.util

import com.google.android.material.slider.Slider
import com.shicheeng.copymanga.data.MangaReaderPage
import com.shicheeng.copymanga.viewmodel.ReaderViewModel

class ReaderSliderAttach(
    private val callBack: PageSelectPosition,
    private val viewModel: ReaderViewModel,
) : Slider.OnChangeListener {

    override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
        if (fromUser) {
            this moveTo value.toInt()
        }
    }

    fun attach(slider: Slider) {
        slider.addOnChangeListener(this)
    }


    private infix fun moveTo(position: Int) {
        val pages = viewModel.currentChapterPage
        val page = pages[position]
        callBack.onPositionCallBack(page)
    }

}

interface PageSelectPosition {
    fun onPositionCallBack(page: MangaReaderPage)
}
