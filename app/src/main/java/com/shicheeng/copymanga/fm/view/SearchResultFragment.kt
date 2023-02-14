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
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shicheeng.copymanga.adapter.MangaListAdapter
import com.shicheeng.copymanga.adapter.MangaLoadStateAdapter
import com.shicheeng.copymanga.databinding.FragmentSearchResultBinding
import com.shicheeng.copymanga.util.applySpanCountWithFooter
import com.shicheeng.copymanga.viewmodel.SearchResultViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class SearchResultFragment : Fragment() {

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private val searchResultViewModel by viewModels<SearchResultViewModel>()
    private val searchResultFragmentArgs by navArgs<SearchResultFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val word = searchResultFragmentArgs.searchWord

        val pagerAdapter = MangaListAdapter()
        val concatAdapter =
            pagerAdapter.withLoadStateFooter(MangaLoadStateAdapter(pagerAdapter::retry))
        val gridLayoutManager =
            GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false).apply {
                applySpanCountWithFooter(concatAdapter)
            }
        binding.searchResultList.apply {
            adapter = concatAdapter
            layoutManager = gridLayoutManager
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchResultViewModel.loadSearch(word).collectLatest {
                    pagerAdapter.submitData(it)
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pagerAdapter.loadStateFlow.collectLatest { loadState ->
                    binding.searchResultListIndicator.isVisible =
                        loadState.refresh is LoadState.Loading
                    binding.searchResultList.isVisible =
                        loadState.refresh is LoadState.NotLoading
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