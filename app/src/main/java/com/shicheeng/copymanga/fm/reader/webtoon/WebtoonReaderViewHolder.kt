package com.shicheeng.copymanga.fm.reader.webtoon

import android.net.Uri
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.decoder.SkiaPooledImageRegionDecoder
import com.shicheeng.copymanga.databinding.ItemPageWebtoonBinding
import com.shicheeng.copymanga.fm.domain.PagerLoader
import com.shicheeng.copymanga.fm.reader.BaseReaderViewHolder

class WebtoonReaderViewHolder(
    itemPageWebtoonBinding: ItemPageWebtoonBinding,
    imageLoader: PagerLoader,
    owner: LifecycleOwner,
) : BaseReaderViewHolder<ItemPageWebtoonBinding>(
    binding = itemPageWebtoonBinding,
    imageLoader = imageLoader
) {

    private var url: String? = null
    private var scrollToRestore = 0

    init {
        binding.bivPagerWebtoon.bindToLifecycle(owner)
        binding.bivPagerWebtoon.regionDecoderFactory = SkiaPooledImageRegionDecoder.Factory()
        binding.bivPagerWebtoon.addOnImageEventListener(delegate)
    }

    override fun onBind(url: String) {
        this.url = url
    }

    override fun onLoadingStarted() {
        binding.errorLayout.errorTextLayout.isVisible = false
        bindingInfo.loadIndicator.isVisible = true
        binding.bivPagerWebtoon.recycle()
    }

    override fun onError(e: Throwable) {
        with(binding.errorLayout) {
            errorTextLayout.isVisible = true
            errorTextTipDesc.text = e.message
            btnErrorRetry.setOnClickListener {
                url?.let { it1 ->
                    delegate.retry(it1)
                }
            }
        }
        bindingInfo.loadIndicator.isVisible = false
    }

    override fun onImageReady(uri: Uri) {
        binding.bivPagerWebtoon.setImage(ImageSource.Uri(uri))
    }

    override fun onImageShowing() {
        with(binding.bivPagerWebtoon) {
            minimumScaleType = SubsamplingScaleImageView.SCALE_TYPE_CUSTOM
            minScale = width / sWidth.toFloat()
            maxScale = minScale
            scrollTo(
                when {
                    scrollToRestore != 0 -> scrollToRestore
                    itemView.top < 0 -> getScrollRange()
                    else -> 0
                },
            )
            scrollToRestore = 0
        }
    }

    override fun onImageShown() {
        bindingInfo.loadIndicator.isVisible = false
        binding.errorLayout.errorTextLayout.isVisible = false
    }

    override fun onRecycler() {
        super.onRecycler()
        binding.bivPagerWebtoon.recycle()
    }

}