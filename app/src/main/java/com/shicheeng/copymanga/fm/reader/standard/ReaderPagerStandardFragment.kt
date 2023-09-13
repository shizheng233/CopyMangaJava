package com.shicheeng.copymanga.fm.reader.standard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MangaReaderPage
import com.shicheeng.copymanga.data.MangaState
import com.shicheeng.copymanga.databinding.FragmentReaderNormalBinding
import com.shicheeng.copymanga.fm.domain.PagerLoader
import com.shicheeng.copymanga.fm.reader.BaseReader
import com.shicheeng.copymanga.fm.reader.BaseReaderAdapter
import com.shicheeng.copymanga.fm.reader.noraml.ReaderPageAdapter
import com.shicheeng.copymanga.util.onPageChangeCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.yield
import javax.inject.Inject

@AndroidEntryPoint
class ReaderPagerStandardFragment : BaseReader<FragmentReaderNormalBinding>() {

    @Inject
    lateinit var pagerLoader: PagerLoader

    override fun onCreateViewInflater(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentReaderNormalBinding {
        return FragmentReaderNormalBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mangaReaderViewpager2.layoutDirection = View.LAYOUT_DIRECTION_LTR

        with(binding.mangaReaderViewpager2) {
            adapter = readerAdapter
            offscreenPageLimit = 1
            onPageChangeCallback {
                viewModel.onPagePositionChange(it)
            }
        }
    }

    override suspend fun onLoadUrlChangeSuccess(list: List<MangaReaderPage>, state: MangaState?) {
        coroutineScope {
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
                    binding.mangaReaderViewpager2.setCurrentItem(position, false)
                    viewModel.onPagePositionChange(position)
                } else {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.no_content),
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                }
            } else {
                items.await()
            }
        }
    }

    override fun createAdapter(): BaseReaderAdapter<*> {
        return ReaderPageAdapter(
            owner = viewLifecycleOwner,
            imageLoader = pagerLoader
        )
    }

    override fun currentState(): MangaState? = bindingOrNull()?.run {
        val adapter = mangaReaderViewpager2.adapter as? BaseReaderAdapter<*>
        val page = adapter?.getItemOrNull(mangaReaderViewpager2.currentItem) ?: return@run null
        MangaState(page.uuid ?: return@run null, page.index)
    }

    override fun moveToPosition(position: Int, smooth: Boolean) {
        binding.mangaReaderViewpager2.setCurrentItem(
            position,
            smooth
        )
    }

    override fun moveDelta(delta: Int) {
        binding.mangaReaderViewpager2.currentItem = binding.mangaReaderViewpager2.currentItem + 1
    }
}