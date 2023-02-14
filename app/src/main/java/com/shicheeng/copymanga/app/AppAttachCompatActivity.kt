package com.shicheeng.copymanga.app

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.google.android.material.appbar.AppBarLayout

open class AppAttachCompatActivity:AppCompatActivity() {

    fun windowsPaddingUp(viewRoot: View, bar: AppBarLayout) {
        ViewCompat.setOnApplyWindowInsetsListener(viewRoot) { view: View, windowInsetsCompat: WindowInsetsCompat ->
            val insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                rightMargin = insets.right
            }
            bar.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }
    }

    fun windowsPaddingUp(viewRoot: View, bar: AppBarLayout, bottomView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(viewRoot) { view: View, windowInsetsCompat: WindowInsetsCompat ->
            val insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                rightMargin = insets.right
            }
            bar.updatePadding(top = insets.top)
            bottomView.updatePadding(bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    fun windowsBottomMarginUp(viewRoot: View, bar: AppBarLayout, bottomView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(viewRoot) { view: View, windowInsetsCompat: WindowInsetsCompat ->
            val insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                rightMargin = insets.right
                bottomMargin = insets.bottom
            }
            bar.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }
    }

}