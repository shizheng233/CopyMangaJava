package com.shicheeng.copymanga.app

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar

open class AppAttachCompatActivity : AppCompatActivity() {

    fun windowsPaddingUp(viewRoot: View, bar: AppBarLayout) {
        ViewCompat.setOnApplyWindowInsetsListener(viewRoot) { view: View, windowInsetsCompat: WindowInsetsCompat ->
            val insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            onInsetsAttach(insets)
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                rightMargin = insets.right
            }
            bar.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }
    }

    inline fun windowsInsets(root: View, crossinline update: (v: View, insets: Insets) -> Unit) {
        ViewCompat.setOnApplyWindowInsetsListener(root) { view: View, windowInsetsCompat: WindowInsetsCompat ->
            val insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            update(view, insets)
            WindowInsetsCompat.CONSUMED
        }
    }

    protected open fun onInsetsAttach(insets: Insets) {

    }

}