package com.shicheeng.copymanga.fm.reader

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.shicheeng.copymanga.databinding.LayoutImageLoadBinding
import com.shicheeng.copymanga.fm.domain.PageHolderDelegate
import com.shicheeng.copymanga.fm.domain.PagerLoader

@Suppress("LeakingThis")
abstract class BaseReaderViewHolder<VB : ViewBinding>(
    protected val binding: VB,
    imageLoader: PagerLoader,
) :
    ViewHolder(binding.root), PageHolderDelegate.Callback {

    val context: Context get() = itemView.context
    protected val bindingInfo = LayoutImageLoadBinding.bind(binding.root)
    protected val delegate = PageHolderDelegate(imageLoader, this)


    fun bind(url: String) {
        delegate.onBind(url)
    }

    open fun onRecycler() {
        delegate.onRecycler()
    }

    abstract fun onBind(url: String)

}