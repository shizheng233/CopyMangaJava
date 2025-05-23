package com.shicheeng.copymanga.fm.reader.webtoon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MangaReaderPage
import com.shicheeng.copymanga.data.MangaState
import com.shicheeng.copymanga.databinding.FragmentReaderWebtoonBinding
import com.shicheeng.copymanga.fm.domain.PagerLoader
import com.shicheeng.copymanga.fm.reader.BaseReader
import com.shicheeng.copymanga.fm.reader.BaseReaderAdapter
import com.shicheeng.copymanga.util.findCurrentPagePosition
import com.shicheeng.copymanga.util.firstVisibleItemPosition
import com.shicheeng.copymanga.util.setFirstVisibleItemPositionSmooth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.yield
import javax.inject.Inject

@AndroidEntryPoint
class WebtoonReaderFragment : BaseReader<FragmentReaderWebtoonBinding>() {

    private val scrollInterpolator = AccelerateDecelerateInterpolator()

    @Inject
    lateinit var pagerLoader: PagerLoader

    override fun onCreateViewInflater(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentReaderWebtoonBinding =
        FragmentReaderWebtoonBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        with(binding.mangaReaderWebtoonRecyclerview) {
            setHasFixedSize(true)
            adapter = readerAdapter
            addOnScrollListener(RecyclerViewScrollListener())
        }

    }

    override fun currentState(): MangaState? = bindingOrNull()?.run {
        val firstItem = mangaReaderWebtoonRecyclerview.findCurrentPagePosition()
        val adapter = mangaReaderWebtoonRecyclerview.adapter as? BaseReaderAdapter<*>
        val page = adapter?.getItemOrNull(firstItem) ?: return@run null
        MangaState(page.uuid ?: return@run null, page.index)
    }

    override fun onDestroyView() {
        requireBinding().mangaReaderWebtoonRecyclerview.adapter = null
        super.onDestroyView()
    }

    override fun moveToPosition(position: Int, smooth: Boolean) {
        binding.mangaReaderWebtoonRecyclerview.setFirstVisibleItemPositionSmooth(
            position,
            smooth
        )
    }

    override fun moveDelta(delta: Int) {
        binding.mangaReaderWebtoonRecyclerview.smoothScrollBy(
            0,
            (binding.mangaReaderWebtoonRecyclerview.height * 0.9).toInt() * delta,
            scrollInterpolator,
        )
    }

    override suspend fun onLoadUrlChangeSuccess(
        list: List<MangaReaderPage>,
        state: MangaState?,
    ) = coroutineScope {
        val items = async {
            requireAdapter().subItems(list)
            yield()
        }
        if (state != null) {
            val position = list.indexOfFirst {
                it.uuid == state.uuid && it.index == state.page
            }
            items.await()
            if (position != -1) {
                with(binding.mangaReaderWebtoonRecyclerview) {
                    firstVisibleItemPosition = position
                }
                viewModel.onPagePositionChange(position)
            } else {
                Snackbar.make(requireView(), getString(R.string.no_content), Snackbar.LENGTH_LONG)
                    .show()
            }
        } else {
            items.await()
        }

    }

    override fun createAdapter(): BaseReaderAdapter<*> {
        return WebtoonReaderAdapter(
            owner = viewLifecycleOwner,
            imageLoader = pagerLoader
        )
    }

    inner class RecyclerViewScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            viewModel.onPagePositionChange(recyclerView.findCurrentPagePosition())
        }
    }

}