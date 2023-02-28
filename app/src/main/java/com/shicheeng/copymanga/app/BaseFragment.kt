package com.shicheeng.copymanga.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment(), View.OnAttachStateChangeListener {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = onViewBindingIn(inflater, container)
        binding.root.addOnAttachStateChangeListener(this)
        return binding.root
    }

    override fun onViewAttachedToWindow(v: View) {
        val insetsCompat = ViewCompat.getRootWindowInsets(v)
        val systemBarInsets = insetsCompat?.getInsets(WindowInsetsCompat.Type.systemBars())
        onFragmentInsets(systemBarInsets, v)
    }

    override fun onViewDetachedFromWindow(v: View) {

    }

    abstract fun onFragmentInsets(systemBarInsets: Insets?, view: View)

    abstract fun onViewBindingIn(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}