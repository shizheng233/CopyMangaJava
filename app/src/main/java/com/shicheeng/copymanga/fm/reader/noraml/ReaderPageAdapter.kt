package com.shicheeng.copymanga.fm.reader.noraml

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.shicheeng.copymanga.databinding.ItemPageBinding
import com.shicheeng.copymanga.fm.domain.PagerLoader
import com.shicheeng.copymanga.fm.reader.BaseReaderAdapter

class ReaderPageAdapter(private val owner: LifecycleOwner,private val imageLoader: PagerLoader) : BaseReaderAdapter<ReaderPageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ReaderPageViewHolder = ReaderPageViewHolder(
        ItemPageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        imageLoader, owner
    )
}