package com.shicheeng.copymanga.fm.reader.noraml

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.shicheeng.copymanga.data.MangaReaderPage
import com.shicheeng.copymanga.data.MangaState
import com.shicheeng.copymanga.databinding.FragmentReaderNormalBinding
import com.shicheeng.copymanga.fm.reader.BaseReader
import com.shicheeng.copymanga.fm.reader.BaseReaderAdapter
import kotlinx.coroutines.async

open class ReaderPageFragment : BaseReader<FragmentReaderNormalBinding>() {

    private var pageAdapter: ReaderPageAdapter? = null

    override fun onCreateViewInflater(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentReaderNormalBinding = FragmentReaderNormalBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mangaReaderViewpager2.layoutDirection = changeDirection()
        pageAdapter = ReaderPageAdapter(viewLifecycleOwner, viewModel.pagerLoaderIn)

        with(binding.mangaReaderViewpager2) {
            adapter = pageAdapter
            offscreenPageLimit = 1
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.onPagePositionChange(position)
                }

            })
        }
    }

    open fun changeDirection(): Int {
        return View.LAYOUT_DIRECTION_RTL
    }

    override fun onDestroyView() {
        pageAdapter = null
        super.onDestroyView()
    }

    override fun moveToPosition(position: Int, smooth: Boolean) {
        binding.mangaReaderViewpager2.setCurrentItem(position, smooth)
    }

    override fun currentState(): MangaState? = bindingOrNull()?.run {
        val adapter = mangaReaderViewpager2.adapter as? BaseReaderAdapter<*>
        val page = adapter?.getItemOrNull(mangaReaderViewpager2.currentItem) ?: return@run null
        MangaState(page.uuid ?: return@run null, page.index)
    }

    override fun moveDelta(delta: Int) {
        binding.mangaReaderViewpager2.currentItem =
            binding.mangaReaderViewpager2.currentItem + delta
    }

    override fun onLoadUrlChangeSuccess(list: List<MangaReaderPage>, state: MangaState?) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            val items = async {
                pageAdapter?.subItems(list)
            }
            if (state != null) {
                val position = list.indexOfFirst {
                    it.uuid == state.uuid && it.index == state.page
                }
                items.await() ?: return@launchWhenCreated
                if (position != -1) {
                    binding.mangaReaderViewpager2.setCurrentItem(position, false)
                    viewModel.onPagePositionChange(position)
                }
            } else {
                items.await()
            }
        }
    }

}