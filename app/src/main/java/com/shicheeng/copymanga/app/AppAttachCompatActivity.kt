package com.shicheeng.copymanga.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

open class AppAttachCompatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
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
            WindowInsetsCompat.Builder()
                .setInsets(WindowInsetsCompat.Type.systemBars(), Insets.NONE)
                .build()
        }
    }


}