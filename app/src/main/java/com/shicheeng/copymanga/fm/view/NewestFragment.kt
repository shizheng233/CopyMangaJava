package com.shicheeng.copymanga.fm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shicheeng.copymanga.adapter.MangaListAdapter
import com.shicheeng.copymanga.adapter.MangaLoadStateAdapter
import com.shicheeng.copymanga.databinding.MangaListBinding
import com.shicheeng.copymanga.util.applySpanCountWithFooter
import com.shicheeng.copymanga.viewmodel.MangaListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewestFragment : Fragment() {

    private var _binding: MangaListBinding? = null
    private val binding get() = _binding!!
    private val listViewModel: MangaListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = MangaListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pagerAdapter = MangaListAdapter()
        val concatAdapter =
            pagerAdapter.withLoadStateFooter(MangaLoadStateAdapter(pagerAdapter::retry))
        val gridLayoutManager =
            GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false).apply {
                applySpanCountWithFooter(concatAdapter)
            }
        binding.mangaListTotalRec.apply {
            adapter = concatAdapter
            layoutManager = gridLayoutManager
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                listViewModel.pageMangaNewestFlow.collectLatest {
                    pagerAdapter.submitData(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pagerAdapter.loadStateFlow.collectLatest { loadState ->
                    binding.linearMangaListBar.isVisible = loadState.refresh is LoadState.Loading
                    binding.mangaListTotalRec.isVisible = loadState.refresh is LoadState.NotLoading
                    binding.errorLayout.errorTextLayout.isVisible =
                        loadState.refresh is LoadState.Error
                    if (loadState.refresh is LoadState.Error) {
                        binding.errorLayout.errorTextTipDesc.text =
                            (loadState.refresh as LoadState.Error).error.message
                        binding.errorLayout.btnErrorRetry.setOnClickListener {
                            pagerAdapter.retry()
                        }
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}