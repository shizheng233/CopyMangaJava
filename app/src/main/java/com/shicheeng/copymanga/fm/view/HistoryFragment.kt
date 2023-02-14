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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shicheeng.copymanga.MyApp
import com.shicheeng.copymanga.adapter.MangaHistoryListAdapter
import com.shicheeng.copymanga.databinding.FragmentMangaHistoryBinding
import com.shicheeng.copymanga.viewmodel.MangaHistoryViewModel
import com.shicheeng.copymanga.viewmodel.MangaHistoryViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: FragmentMangaHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MangaHistoryViewModel> {
        MangaHistoryViewModelFactory((requireActivity().application as MyApp).repo)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMangaHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterZ = MangaHistoryListAdapter()
        binding.mangaListHistoryMain.apply {
            adapter = adapterZ
            layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.history.collectLatest {
                    if (it.isEmpty()) {
                        binding.mangaListTextTip.isVisible = true
                        binding.mangaListHistoryMain.isVisible = false
                        binding.mangaListIndicator.isVisible = false
                    } else {
                        binding.mangaListTextTip.isVisible = false
                        binding.mangaListHistoryMain.isVisible = true
                        binding.mangaListIndicator.isVisible = false
                        adapterZ.submitList(it)
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