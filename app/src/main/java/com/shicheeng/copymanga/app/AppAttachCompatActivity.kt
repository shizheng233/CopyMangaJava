package com.shicheeng.copymanga.app

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

open class AppAttachCompatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }


    fun windowsPaddingUp(
        viewRoot: View,
        bar: AppBarLayout,
        bottomNavigationView: BottomNavigationView,
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(viewRoot) { view: View, windowInsetsCompat: WindowInsetsCompat ->
            val insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            onInsetsAttach(insets)
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                rightMargin = insets.right
            }
            bar.updatePadding(top = insets.top)
            bottomNavigationView.updatePadding(bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    inline fun windowsInsets(
        root: View,
        crossinline update: Insets.(v: View, gestureInsets: Insets) -> Unit,
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(root) { view: View, windowInsetsCompat: WindowInsetsCompat ->
            val insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            val systemGestureInsets =
                windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemGestures())
            update(insets, view, systemGestureInsets)
            WindowInsetsCompat.CONSUMED
        }
    }


    protected open fun onInsetsAttach(insets: Insets) {

    }

}