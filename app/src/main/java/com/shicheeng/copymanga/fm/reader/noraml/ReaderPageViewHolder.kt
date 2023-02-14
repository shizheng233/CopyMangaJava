package com.shicheeng.copymanga.fm.reader.noraml

import android.net.Uri
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.davemorrissey.labs.subscaleview.ImageSource
import com.shicheeng.copymanga.databinding.ItemPageBinding
import com.shicheeng.copymanga.fm.domain.PagerLoader
import com.shicheeng.copymanga.fm.reader.BaseReaderViewHolder

class ReaderPageViewHolder(
    binding: ItemPageBinding,
    imageLoader: PagerLoader,
    owner: LifecycleOwner,
) :
    BaseReaderViewHolder<ItemPageBinding>(binding, imageLoader) {

    init {
        binding.bivPager.bindToLifecycle(owner)
        binding.bivPager.addOnImageEventListener(delegate)
    }

    private var url: String? = null

    override fun onBind(url: String, onHolderImageClick: (View) -> Unit) {

        binding.bivPager.setOnClickListener {
            onHolderImageClick.invoke(it)
        }
        this.url = url
    }

    override fun onLoadingStarted() {
        binding.errorLayout.errorTextLayout.isVisible = false
        bindingInfo.loadIndicator.isVisible = true
        binding.bivPager.recycle()
    }

    override fun onError(e: Throwable) {
        with(binding.errorLayout) {
            errorTextLayout.isVisible = true
            errorTextTipDesc.text = e.message
            btnErrorRetry.setOnClickListener {
                url?.let { it1 -> delegate.retry(it1) }
            }
        }
        bindingInfo.loadIndicator.isVisible = false
    }

    override fun onImageReady(uri: Uri) {
        binding.bivPager.setImage(ImageSource.Uri(uri))
    }

    override fun onImageShown() {
        bindingInfo.loadIndicator.isVisible = false
        binding.errorLayout.errorTextLayout.isVisible = false
    }

    override fun onRecycler() {
        super.onRecycler()
        binding.bivPager.recycle()
    }


}