package com.shicheeng.copymanga.fm.reader.webtoon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.shicheeng.copymanga.databinding.ItemPageWebtoonBinding
import com.shicheeng.copymanga.fm.domain.PagerLoader
import com.shicheeng.copymanga.fm.reader.BaseReaderAdapter

class WebtoonReaderAdapter(
    private val owner: LifecycleOwner,
    private val imageLoader: PagerLoader,
) : BaseReaderAdapter<WebtoonReaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): WebtoonReaderViewHolder =
        WebtoonReaderViewHolder(ItemPageWebtoonBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false), imageLoader, owner)

}