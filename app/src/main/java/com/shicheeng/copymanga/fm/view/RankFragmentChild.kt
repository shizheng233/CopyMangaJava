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
import com.shicheeng.copymanga.databinding.RankMangaFragmentBinding
import com.shicheeng.copymanga.util.applySpanCountWithFooter
import com.shicheeng.copymanga.viewmodel.RankViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RankFragmentChild : Fragment() {

    companion object {
        fun newInstance(type: String): RankFragmentChild {
            val args = Bundle()
            args.putString("type", type)
            val fragment = RankFragmentChild()
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: RankMangaFragmentBinding? = null
    private val binding get() = _binding!!
    private val rankViewModel: RankViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = RankMangaFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val type = arguments?.getString("type")!!
        val adapter = MangaListAdapter()
        val footer = MangaLoadStateAdapter { adapter.retry() }
        val concatAdapter = adapter.withLoadStateFooter(footer)
        val gridLayoutManager =
            GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false).apply {
                applySpanCountWithFooter(concatAdapter)
            }
        binding.recyclerFragmentRank.layoutManager = gridLayoutManager
        binding.recyclerFragmentRank.adapter = concatAdapter
        binding.intoBindUiRank(adapter)
        intoRankData(type, adapter)
    }

    private fun intoRankData(type: String, adapter: MangaListAdapter) {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                rankViewModel.loadRank(type).collectLatest {
                    adapter.submitData(it)
                }
            }
        }
    }

    private fun RankMangaFragmentBinding.intoBindUiRank(adapter: MangaListAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest {
                    progressCircularRank.isVisible = it.refresh is LoadState.Loading
                    recyclerFragmentRank.isVisible = it.refresh is LoadState.NotLoading
                    errorLayout.errorTextLayout.isVisible =
                        it.refresh is LoadState.Error
                    if (it.refresh is LoadState.Error) {
                        binding.errorLayout.errorTextTipDesc.text =
                            (it.refresh as LoadState.Error).error.message
                        binding.errorLayout.btnErrorRetry.setOnClickListener {
                            adapter.retry()
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