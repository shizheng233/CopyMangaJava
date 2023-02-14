package com.shicheeng.copymanga.fm.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import com.shicheeng.copymanga.data.MangaReaderPage
import com.shicheeng.copymanga.data.MangaState
import com.shicheeng.copymanga.viewmodel.ReaderViewModel

abstract class BaseReader<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected val viewModel by activityViewModels<ReaderViewModel>()
    protected val binding: VB get() = checkNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = onCreateViewInflater(inflater, container)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.mangaContent.observe(viewLifecycleOwner) {
            onLoadUrlChangeSuccess(it.list, it.state)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    protected fun bindingOrNull() = _binding

    protected abstract fun onCreateViewInflater(inflater: LayoutInflater, container: ViewGroup?): VB

    protected abstract fun onLoadUrlChangeSuccess(
        list: List<MangaReaderPage>,
        state: MangaState?,
    )

    abstract fun currentState():MangaState?

    abstract fun moveToPosition(position: Int, smooth: Boolean)

    abstract fun moveDelta(delta: Int)


}