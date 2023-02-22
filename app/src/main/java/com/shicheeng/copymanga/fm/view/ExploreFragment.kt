package com.shicheeng.copymanga.fm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shicheeng.copymanga.adapter.MangaListAdapter
import com.shicheeng.copymanga.adapter.MangaLoadStateAdapter
import com.shicheeng.copymanga.app.BaseFragment
import com.shicheeng.copymanga.databinding.HotMangaLayoutBinding
import com.shicheeng.copymanga.dialog.SortDialogFragment
import com.shicheeng.copymanga.util.KeyWordSwap
import com.shicheeng.copymanga.util.applySpanCountWithFooter
import com.shicheeng.copymanga.viewmodel.ExploreMangaViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ExploreFragment : BaseFragment<HotMangaLayoutBinding>() {

    private val exploreMangaViewModel by viewModels<ExploreMangaViewModel>()

    override fun onViewBindingIn(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): HotMangaLayoutBinding = HotMangaLayoutBinding.inflate(inflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mangaListAdapter = MangaListAdapter()
        val concatAdapter =
            mangaListAdapter.withLoadStateFooter(MangaLoadStateAdapter(mangaListAdapter::retry))
        val gridLayoutManager =
            GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false).apply {
                applySpanCountWithFooter(concatAdapter)
            }


        binding.recyclerMangaHot.apply {
            adapter = concatAdapter
            layoutManager = gridLayoutManager
        }

        val filterDialog = SortDialogFragment()
        binding.fabHot.setOnClickListener {
            filterDialog.show(childFragmentManager, KeyWordSwap.FLAG_)
        }
        filterDialog.setOnButtonClickListener { v, order, theme ->
            viewLifecycleOwner.lifecycleScope.launch {
                exploreMangaViewModel.order.emit(order)
                exploreMangaViewModel.themeType.emit(theme)
            }
            mangaListAdapter.refresh()
            binding.recyclerMangaHot.scrollToPosition(0)
            filterDialog.dismiss()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                exploreMangaViewModel.loadFilterResult.collectLatest {
                    mangaListAdapter.submitData(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mangaListAdapter.loadStateFlow.collectLatest { loadState ->
                    binding.progressIndicatorHot.isVisible = loadState.refresh is LoadState.Loading
                    binding.recyclerMangaHot.isVisible = loadState.refresh is LoadState.NotLoading
                    binding.errorLayout.errorTextLayout.isVisible =
                        loadState.refresh is LoadState.Error
                    if (loadState.refresh is LoadState.Error) {
                        binding.errorLayout.errorTextTipDesc.text =
                            (loadState.refresh as LoadState.Error).error.message
                        binding.errorLayout.btnErrorRetry.setOnClickListener {
                            mangaListAdapter.retry()
                        }
                    }
                }
            }
        }

    }

    override fun onFragmentInsets(systemBarInsets: Insets?, view: View) {
        if (systemBarInsets == null) {
            return
        }
        binding.fabHot.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = binding.fabHot.marginBottom + systemBarInsets.bottom
        }
    }

}